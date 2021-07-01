package com.codingwithmitch.openapi.ui.main.create_blog

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
import com.codingwithmitch.openapi.ui.AreYouSureCallback
import com.codingwithmitch.openapi.ui.main.create_blog.state.CREATE_BLOG_VIEW_STATE_BUNDLE_KEY
import com.codingwithmitch.openapi.ui.main.create_blog.state.CreateBlogStateEvent
import com.codingwithmitch.openapi.ui.main.create_blog.state.CreateBlogViewState
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.ERROR_MUST_SELECT_IMAGE
import com.codingwithmitch.openapi.util.MessageType
import com.codingwithmitch.openapi.util.Response
import com.codingwithmitch.openapi.util.StateMessageCallback
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.SUCCESS_BLOG_CREATED
import com.codingwithmitch.openapi.util.UIComponentType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_blog.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class CreateBlogFragment : BaseCreateBlogFragment(R.layout.fragment_create_blog) {

	@Inject
	lateinit var options: RequestOptions

	private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri>() {
		override fun createIntent(context: Context, input: Any?): Intent {
			return CropImage
				.activity()
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
			(inState[CREATE_BLOG_VIEW_STATE_BUNDLE_KEY] as CreateBlogViewState?)?.let { viewState ->
				viewModel.setViewState(viewState)
			}
		}
		cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
			uri?.let { resultUri ->
				viewModel.setNewBlogFields(
					title = null,
					body = null,
					uri = resultUri
				)
			}
		}
	}

	/**
	 * !IMPORTANT!
	 * Must save ViewState b/c in event of process death the LiveData in ViewModel will be lost
	 */
	override fun onSaveInstanceState(outState: Bundle) {
		outState.putParcelable(
			CREATE_BLOG_VIEW_STATE_BUNDLE_KEY,
			viewModel.viewState.value
		)
		super.onSaveInstanceState(outState)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setHasOptionsMenu(true)

		blog_image.setOnClickListener {
			if (uiCommunicationListener.isStoragePermissionGranted()) {
				cropActivityResultLauncher.launch(null)
			}
		}

		update_textview.setOnClickListener {
			if (uiCommunicationListener.isStoragePermissionGranted()) {
				cropActivityResultLauncher.launch(null)
			}
		}

		subscribeObservers()
	}

	fun subscribeObservers() {
		viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
			viewState?.blogFields?.let { newBlogFields ->
				setBlogProperties(
					newBlogFields.newBlogTitle,
					newBlogFields.newBlogBody,
					newBlogFields.newImageUri
				)
			}
		})

		viewModel.numActiveJobs.observe(viewLifecycleOwner, {
			uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
		})

		viewModel.stateMessage.observe(viewLifecycleOwner, { stateMessage ->

			stateMessage?.let {
				if (it.response.message.equals(SUCCESS_BLOG_CREATED)) {
					viewModel.clearNewBlogFields()
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

	fun setBlogProperties(
		title: String? = "",
		body: String? = "",
		image: Uri?
	) {
		if (image != null) {
			Glide.with(this)
				.setDefaultRequestOptions(options)
				.load(image)
				.into(blog_image)
		} else {
			Glide.with(this)
				.setDefaultRequestOptions(options)
				.load(R.drawable.default_image)
				.into(blog_image)
		}

		blog_title.setText(title)
		blog_body.setText(body)
	}

	private fun publishNewBlog() {
		var multipartBody: MultipartBody.Part? = null
		viewModel.viewState.value?.blogFields?.newImageUri?.let { imageUri ->
			imageUri.path?.let { filePath ->
				val imageFile = File(filePath)
				Log.d(TAG, "CreateBlogFragment, imageFile: file: $imageFile")
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

		multipartBody?.let {

			viewModel.setStateEvent(
				CreateBlogStateEvent.CreateNewBlogEvent(
					blog_title.text.toString(),
					blog_body.text.toString(),
					it
				)
			)
			uiCommunicationListener.hideSoftKeyboard()
		} ?: ERROR_MUST_SELECT_IMAGE.showErrorDialog()
	}

	private fun String.showErrorDialog() {
		uiCommunicationListener.onResponseReceived(
			response = Response(
				message = this,
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

	override fun onPause() {
		super.onPause()
		viewModel.setNewBlogFields(
			blog_title.text.toString(),
			blog_body.text.toString(),
			null
		)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.publish_menu, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.publish -> {
				val callback: AreYouSureCallback = object : AreYouSureCallback {

					override fun proceed() {
						publishNewBlog()
					}

					override fun cancel() {
						// ignore
					}

				}
				uiCommunicationListener.onResponseReceived(
					response = Response(
						message = getString(R.string.are_you_sure_publish),
						uiComponentType = UIComponentType.AreYouSureDialog(callback),
						messageType = MessageType.Info()
					),
					stateMessageCallback = object : StateMessageCallback {
						override fun removeMessageFromStack() {
							viewModel.clearStateMessage()
						}
					}
				)
				return true
			}
		}
		return super.onOptionsItemSelected(item)
	}
}










