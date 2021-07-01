package com.codingwithmitch.openapi.ui.main.blog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.main.blog.state.BLOG_VIEW_STATE_BUNDLE_KEY
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState
import com.codingwithmitch.openapi.ui.main.blog.viewmodel.*
import com.codingwithmitch.openapi.util.Constants.Companion.GALLERY_REQUEST_CODE
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.SOMETHING_WRONG_WITH_IMAGE
import com.codingwithmitch.openapi.util.MessageType
import com.codingwithmitch.openapi.util.Response
import com.codingwithmitch.openapi.util.StateMessageCallback
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.SUCCESS_BLOG_UPDATED
import com.codingwithmitch.openapi.util.UIComponentType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_update_blog.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@FlowPreview
@AndroidEntryPoint
@ExperimentalCoroutinesApi
class UpdateBlogFragment : BaseBlogFragment(R.layout.fragment_update_blog) {

	companion object {
		private const val TAG: String = "AppDebug"
	}

	@Inject
	lateinit var options: RequestOptions

	private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri>() {
		override fun createIntent(context: Context, input: Any?): Intent {
			return CropImage.activity()
				.setGuidelines(CropImageView.Guidelines.ON)
				.getIntent(context)
		}

		override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
			return CropImage.getActivityResult(intent)?.uriContent
		}
	}

	private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		// Restore state after process death
		savedInstanceState?.let { inState ->
			(inState[BLOG_VIEW_STATE_BUNDLE_KEY] as BlogViewState?)?.let { viewState ->
				viewModel.setViewState(viewState)
			}
		}
		cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
			uri?.let { resultUri ->
				viewModel.setUpdatedUri(resultUri)
			}
		}
	}

	/**
	 * !IMPORTANT!
	 * Must save ViewState b/c in event of process death the LiveData in ViewModel will be lost
	 */
	override fun onSaveInstanceState(outState: Bundle) {
		val viewState = viewModel.viewState.value

		//clear the list. Don't want to save a large list to bundle.
		viewState?.blogFields?.blogList = ArrayList()

		outState.putParcelable(
			BLOG_VIEW_STATE_BUNDLE_KEY,
			viewState
		)
		super.onSaveInstanceState(outState)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setHasOptionsMenu(true)
		subscribeObservers()

		image_container.setOnClickListener {
			if (uiCommunicationListener.isStoragePermissionGranted()) {
				cropActivityResultLauncher.launch(null)
			}
		}
	}

	private fun showImageSelectionError() {
		uiCommunicationListener.onResponseReceived(
			response = Response(
				message = SOMETHING_WRONG_WITH_IMAGE,
				uiComponentType = UIComponentType.Dialog,
				messageType = MessageType.Error
			),
			stateMessageCallback = object : StateMessageCallback {
				override fun removeMessageFromStack() {
					viewModel.clearStateMessage()
				}
			}
		)
	}

	fun subscribeObservers() {

		viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
			viewState?.updatedBlogFields?.let { updatedBlogFields ->
				setBlogProperties(
					updatedBlogFields.updatedBlogTitle,
					updatedBlogFields.updatedBlogBody,
					updatedBlogFields.updatedImageUri
				)
			}
		})

		viewModel.numActiveJobs.observe(viewLifecycleOwner, {
			uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
		})

		viewModel.stateMessage.observe(viewLifecycleOwner, { stateMessage ->

			stateMessage?.let {

				if (stateMessage.response.message.equals(SUCCESS_BLOG_UPDATED)) {
					viewModel.updateListItem()
				}

				uiCommunicationListener.onResponseReceived(
					response = it.response,
					stateMessageCallback = object : StateMessageCallback {
						override fun removeMessageFromStack() {
							viewModel.clearStateMessage()
						}
					}
				)
			}
		})
	}

	fun setBlogProperties(title: String?, body: String?, image: Uri?) {
		image?.let {
			Glide.with(this)
				.setDefaultRequestOptions(options)
				.load(it)
				.into(blog_image)
		}
		blog_title.setText(title)
		blog_body.setText(body)
	}

	private fun saveChanges() {
		var multipartBody: MultipartBody.Part? = null
		viewModel.getUpdatedBlogUri()?.let { imageUri ->
			imageUri.path?.let { filePath ->
				val imageFile = File(filePath)
				Log.d(TAG, "UpdateBlogFragment, imageFile: file: $imageFile")
				if (imageFile.exists()) {
					val requestBody =
						RequestBody.create(
							MediaType.parse("image/*"),
							imageFile
						)
					// name = field name in serializer
					// filename = name of the image file
					// requestBody = file with file type information
					multipartBody = MultipartBody.Part.createFormData(
						"image",
						imageFile.name,
						requestBody
					)
				}
			}
		}
		viewModel.setStateEvent(
			BlogStateEvent.UpdateBlogPostEvent(
				blog_title.text.toString(),
				blog_body.text.toString(),
				multipartBody
			)
		)
		uiCommunicationListener.hideSoftKeyboard()
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.update_menu, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.save -> {
				saveChanges()
				return true
			}
		}
		return super.onOptionsItemSelected(item)
	}

	override fun onPause() {
		super.onPause()
		viewModel.setUpdatedTitle(blog_title.text.toString())
		viewModel.setUpdatedBody(blog_body.text.toString())
	}
}