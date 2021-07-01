package com.codingwithmitch.openapi.ui.main.blog

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.ui.AreYouSureCallback
import com.codingwithmitch.openapi.ui.main.blog.state.BLOG_VIEW_STATE_BUNDLE_KEY
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent.CheckAuthorOfBlogPost
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent.DeleteBlogPostEvent
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState
import com.codingwithmitch.openapi.ui.main.blog.viewmodel.*
import com.codingwithmitch.openapi.util.*
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_view_blog.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ViewBlogFragment : BaseBlogFragment(R.layout.fragment_view_blog) {
	@Inject
	lateinit var options: RequestOptions

	companion object {
		private const val TAG: String = "AppDebug"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		// Restore state after process death
		savedInstanceState?.let { inState ->
			(inState[BLOG_VIEW_STATE_BUNDLE_KEY] as BlogViewState?)?.let { viewState ->
				viewModel.setViewState(viewState)
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
		checkIsAuthorOfBlogPost()
		uiCommunicationListener.expandAppBar()

		delete_button.setOnClickListener {
			confirmDeleteRequest()
		}

	}

	private fun confirmDeleteRequest() {
		val callback: AreYouSureCallback = object : AreYouSureCallback {

			override fun proceed() {
				deleteBlogPost()
			}

			override fun cancel() {
				// ignore
			}
		}
		uiCommunicationListener.onResponseReceived(
			response = Response(
				message = getString(R.string.are_you_sure_delete),
				uiComponentType = UIComponentType.AreYouSureDialog(callback),
				messageType = MessageType.Info()
			),
			stateMessageCallback = object : StateMessageCallback {
				override fun removeMessageFromStack() {
					viewModel.clearStateMessage()
				}
			}
		)
	}

	fun deleteBlogPost() {
		viewModel.setStateEvent(
			DeleteBlogPostEvent()
		)
	}

	private fun checkIsAuthorOfBlogPost() {
		viewModel.setIsAuthorOfBlogPost(false) // reset
		viewModel.setStateEvent(CheckAuthorOfBlogPost())
	}

	fun subscribeObservers() {

		viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
			viewState?.viewBlogFields?.blogPost?.let { blogPost ->
				setBlogProperties(blogPost)
			}

			if (viewState?.viewBlogFields?.isAuthorOfBlogPost == true) {
				adaptViewToAuthorMode()
			}
		})

		viewModel.numActiveJobs.observe(viewLifecycleOwner, {
			uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
		})

		viewModel.stateMessage.observe(viewLifecycleOwner, { stateMessage ->

			if (stateMessage?.response?.message.equals(SUCCESS_BLOG_DELETED)) {
				viewModel.removeDeletedBlogPost()
				findNavController().popBackStack()
			}

			stateMessage?.let {
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

	private fun adaptViewToAuthorMode() {
		activity?.invalidateOptionsMenu()
		delete_button.visibility = View.VISIBLE
	}

	fun setBlogProperties(blogPost: BlogPost) {
		Glide.with(this)
			.setDefaultRequestOptions(options)
			.load(blogPost.image)
			.into(blog_image)
		blog_title.text = blogPost.title
		blog_author.text = blogPost.username
		blog_update_date.text = DateUtils.convertLongToStringDate(blogPost.date_updated)
		blog_body.text = blogPost.body
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		if (viewModel.isAuthorOfBlogPost()) {
			inflater.inflate(R.menu.edit_view_menu, menu)
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (viewModel.isAuthorOfBlogPost()) {
			when (item.itemId) {
				R.id.edit -> {
					navUpdateBlogFragment()
					return true
				}
			}
		}
		return super.onOptionsItemSelected(item)
	}

	private fun navUpdateBlogFragment() {
		try {
			// prep for next fragment
			viewModel.setUpdatedTitle(viewModel.getBlogPost().title)
			viewModel.setUpdatedBody(viewModel.getBlogPost().body)
			viewModel.setUpdatedUri(viewModel.getBlogPost().image.toUri())
			findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
		} catch (e: Exception) {
			// send error report or something. These fields should never be null. Not possible
			Log.e(TAG, "Exception: ${e.message}")
		}
	}
}

