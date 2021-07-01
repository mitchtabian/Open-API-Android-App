package com.codingwithmitch.openapi.ui.main.blog

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistence.BlogQueryUtils.Companion.BLOG_FILTER_DATE_UPDATED
import com.codingwithmitch.openapi.persistence.BlogQueryUtils.Companion.BLOG_FILTER_USERNAME
import com.codingwithmitch.openapi.persistence.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.codingwithmitch.openapi.persistence.BlogQueryUtils.Companion.BLOG_ORDER_DESC
import com.codingwithmitch.openapi.ui.main.blog.state.BLOG_VIEW_STATE_BUNDLE_KEY
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState
import com.codingwithmitch.openapi.ui.main.blog.viewmodel.*
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.isPaginationDone
import com.codingwithmitch.openapi.util.StateMessageCallback
import com.codingwithmitch.openapi.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_blog.*
import kotlinx.coroutines.*

@FlowPreview
@ExperimentalCoroutinesApi
class BlogFragment : BaseBlogFragment(R.layout.fragment_blog),
	BlogListAdapter.Interaction,
	SwipeRefreshLayout.OnRefreshListener {
	companion object {
		private const val TAG: String = "AppDebug"
	}

	private lateinit var searchView: SearchView
	private lateinit var recyclerAdapter: BlogListAdapter
	private var requestManager: RequestManager? = null // can leak memory, must be nullable

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		savedInstanceState?.let { inState ->
			Log.d(TAG, "BlogViewState: inState is NOT null")
			(inState[BLOG_VIEW_STATE_BUNDLE_KEY] as BlogViewState?)?.let { viewState ->
				Log.d(TAG, "BlogViewState: restoring view state: $viewState")
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
		(activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
		setHasOptionsMenu(true)
//        swipe_refresh.setOnRefreshListener(this)
		setupGlide()
		initRecyclerView()
		subscribeObservers()
	}

	override fun onResume() {
		super.onResume()
		viewModel.refreshFromCache()
	}

	override fun onPause() {
		super.onPause()
		saveLayoutManagerState()
	}

	private fun saveLayoutManagerState() {
		blog_post_recyclerview.layoutManager?.onSaveInstanceState()?.let { lmState ->
			viewModel.setLayoutManagerState(lmState)
		}
	}

	private fun subscribeObservers() {
		viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
			if (viewState != null) {
				recyclerAdapter.apply {
					viewState.blogFields.blogList?.let {
						preloadGlideImages(
							requestManager = requestManager as RequestManager,
							list = it
						)
					}

					submitList(
						blogList = viewState.blogFields.blogList,
						isQueryExhausted = viewState.blogFields.isQueryExhausted ?: true
					)
				}

			}
		})

		viewModel.numActiveJobs.observe(viewLifecycleOwner, {
			uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
		})

		viewModel.stateMessage.observe(viewLifecycleOwner, { stateMessage ->

			stateMessage?.let {
				if (isPaginationDone(stateMessage.response.message)) {
					viewModel.setQueryExhausted(true)
					viewModel.clearStateMessage()
				} else {
					uiCommunicationListener.onResponseReceived(
						response = it.response,
						stateMessageCallback = object : StateMessageCallback {
							override fun removeMessageFromStack() {
								viewModel.clearStateMessage()
							}
						}
					)
				}
			}
		})
	}

	private fun initSearchView(menu: Menu) {
		activity?.apply {
			val searchManager: SearchManager = getSystemService(SEARCH_SERVICE) as SearchManager
			searchView = menu.findItem(R.id.action_search).actionView as SearchView
			searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
			searchView.maxWidth = Integer.MAX_VALUE
			searchView.setIconifiedByDefault(true)
			searchView.isSubmitButtonEnabled = true
		}

		// ENTER ON COMPUTER KEYBOARD OR ARROW ON VIRTUAL KEYBOARD
		val searchPlate = searchView.findViewById(R.id.search_src_text) as EditText
		searchPlate.setOnEditorActionListener { v, actionId, _ ->

			if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED
				|| actionId == EditorInfo.IME_ACTION_SEARCH
			) {
				val searchQuery = v.text.toString()
				Log.e(TAG, "SearchView: (keyboard or arrow) executing search...: $searchQuery")
				onBlogSearchOrFilter()
			}
			true
		}

		// SEARCH BUTTON CLICKED (in toolbar)
		val searchButton = searchView.findViewById(R.id.search_go_btn) as View
		searchButton.setOnClickListener {
			val searchQuery = searchPlate.text.toString()
			Log.e(TAG, "SearchView: (button) executing search...: $searchQuery")
			onBlogSearchOrFilter()

		}
	}

	private fun onBlogSearchOrFilter() {
		resetUI()
	}

	private fun resetUI() {
		blog_post_recyclerview.smoothScrollToPosition(0)
		uiCommunicationListener.hideSoftKeyboard()
		focusable_view.requestFocus()
	}

	private fun initRecyclerView() {

		blog_post_recyclerview.apply {
			layoutManager = LinearLayoutManager(this@BlogFragment.context)
			val topSpacingDecorator = TopSpacingItemDecoration(30)
			removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
			addItemDecoration(topSpacingDecorator)

			recyclerAdapter = BlogListAdapter(
				requestManager as RequestManager,
				this@BlogFragment
			)
			addOnScrollListener(object : RecyclerView.OnScrollListener() {

				override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
					super.onScrollStateChanged(recyclerView, newState)
					val layoutManager = recyclerView.layoutManager as LinearLayoutManager
					val lastPosition = layoutManager.findLastVisibleItemPosition()
					if (lastPosition == recyclerAdapter.itemCount.minus(1)) {
						Log.d(TAG, "BlogFragment: attempting to load next page...")
						viewModel.nextPage()
					}
				}
			})
			adapter = recyclerAdapter
		}
	}

	private fun setupGlide() {
		val requestOptions = RequestOptions
			.placeholderOf(R.drawable.default_image)
			.error(R.drawable.default_image)

		activity?.let {
			requestManager = Glide.with(it)
				.applyDefaultRequestOptions(requestOptions)
		}
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.search_menu, menu)
		initSearchView(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {

		when (item.itemId) {
			R.id.action_filter_settings -> {
				showFilterDialog()
				return true
			}
		}
		return super.onOptionsItemSelected(item)
	}

	override fun onItemSelected(position: Int, item: BlogPost) {
		viewModel.setBlogPost(item)
		findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
	}

	override fun restoreListPosition() {
		viewModel.viewState.value?.blogFields?.layoutManagerState?.let { lmState ->
			blog_post_recyclerview?.layoutManager?.onRestoreInstanceState(lmState)
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		// clear references (can leak memory)
		blog_post_recyclerview.adapter = null
		requestManager = null
	}

	override fun onRefresh() {
		onBlogSearchOrFilter()
//        swipe_refresh.isRefreshing = false
	}

	private fun showFilterDialog() {

		activity?.let {
			val dialog = MaterialDialog(it)
				.noAutoDismiss()
				.customView(R.layout.layout_blog_filter)

			val view = dialog.getCustomView()

			val filter = viewModel.getFilter()
			val order = viewModel.getOrder()

			view.findViewById<RadioGroup>(R.id.filter_group).apply {
				when (filter) {
					BLOG_FILTER_DATE_UPDATED -> check(R.id.filter_date)
					BLOG_FILTER_USERNAME -> check(R.id.filter_author)
				}
			}

			view.findViewById<RadioGroup>(R.id.order_group).apply {
				when (order) {
					BLOG_ORDER_ASC -> check(R.id.filter_asc)
					BLOG_ORDER_DESC -> check(R.id.filter_desc)
				}
			}

			view.findViewById<TextView>(R.id.positive_button).setOnClickListener {
				Log.d(TAG, "FilterDialog: apply filter.")

				val newFilter =
					when (view.findViewById<RadioGroup>(R.id.filter_group).checkedRadioButtonId) {
						R.id.filter_author -> BLOG_FILTER_USERNAME
						R.id.filter_date -> BLOG_FILTER_DATE_UPDATED
						else -> BLOG_FILTER_DATE_UPDATED
					}

				val newOrder =
					when (view.findViewById<RadioGroup>(R.id.order_group).checkedRadioButtonId) {
						R.id.filter_desc -> "-"
						else -> ""
					}

				viewModel.apply {
					saveFilterOptions(newFilter, newOrder)
					setBlogFilter(newFilter)
					setBlogOrder(newOrder)
				}

				onBlogSearchOrFilter()

				dialog.dismiss()
			}

			view.findViewById<TextView>(R.id.negative_button).setOnClickListener {
				Log.d(TAG, "FilterDialog: cancelling filter.")
				dialog.dismiss()
			}

			dialog.show()
		}
	}


}








