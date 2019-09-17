package com.codingwithmitch.openapi.ui.main.blog

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState
import com.codingwithmitch.openapi.util.TopSpacingItemDecoration
import com.codingwithmitch.openapi.util.ErrorHandling
import kotlinx.android.synthetic.main.fragment_blog.*
import javax.inject.Inject
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.codingwithmitch.openapi.repository.main.BlogQueryUtils.Companion.BLOG_FILTER_DATE_UPDATED
import com.codingwithmitch.openapi.repository.main.BlogQueryUtils.Companion.BLOG_FILTER_USERNAME
import com.codingwithmitch.openapi.repository.main.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.codingwithmitch.openapi.ui.*
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent.*
import com.codingwithmitch.openapi.util.PreferenceKeys.Companion.BLOG_FILTER
import com.codingwithmitch.openapi.util.PreferenceKeys.Companion.BLOG_ORDER

class BlogFragment : BaseBlogFragment(),
    BlogListAdapter.BlogViewHolder.BlogClickListener,
    SharedPreferences.OnSharedPreferenceChangeListener,
    SwipeRefreshLayout.OnRefreshListener
{
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var editor: SharedPreferences.Editor

    private lateinit var searchView: SearchView
    private lateinit var recyclerAdapter: BlogListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        swipe_refresh.setOnRefreshListener(this)
        setHasOptionsMenu(true)
        initRecyclerView()
        subscribeObservers()
        viewModel.loadInitialBlogs()
    }

    // check for end of pagination (with network request)
    // must do this b/c server will return an ApiErrorResponse if page is not valid
    fun checkPaginationEnd(dataState: DataState<BlogViewState>?){
        dataState?.let{
            it.error?.let{ event ->
                event.peekContent().response.message?.let{
                    if(ErrorHandling.isPaginationDone(it)){

                        // handle the error message event so it doesn't display in UI
                        event.getContentIfNotHandled()

                        // set query exhausted to update RecyclerView with "No more results..." list item
                        viewModel.setQueryExhausted(true)
                    }
                }
            }
        }
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            if(dataState != null){
                checkPaginationEnd(dataState)
                stateChangeListener.onDataStateChange(dataState)
                dataState.data?.let {
                    it.data?.let{
                        it.getContentIfNotHandled()?.let{
                            Log.d(TAG, "BlogFragment, DataState: ${it}")
                            Log.d(TAG, "BlogFragment, DataState: isQueryInProgress?: ${it.blogFields.isQueryInProgress}")
                            Log.d(TAG, "BlogFragment, DataState: isQueryExhausted?: ${it.blogFields.isQueryExhausted}")
                            viewModel.setQueryInProgress(it.blogFields.isQueryInProgress)
                            viewModel.setQueryExhausted(it.blogFields.isQueryExhausted)
                            viewModel.setBlogListData(it.blogFields.blogList)
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer{ viewState ->
            Log.d(TAG, "BlogFragment, ViewState: ${viewState}")
            if(viewState != null){
                recyclerAdapter.submitList(
                    viewState.blogFields.blogList,
                    viewState.blogFields.isQueryExhausted
                )
            }
        })
    }

    private fun initRecyclerView(){
        blog_post_recyclerview.layoutManager = LinearLayoutManager(this@BlogFragment.context)
        val topSpacingDecorator = TopSpacingItemDecoration(30)
        blog_post_recyclerview.removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
        blog_post_recyclerview.addItemDecoration(topSpacingDecorator)

        recyclerAdapter = BlogListAdapter(requestManager,  this@BlogFragment)
        blog_post_recyclerview.addOnScrollListener(object: RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == recyclerAdapter.itemCount.minus(1)) {
                    Log.d(TAG, "BlogFragment: attempting to load next page...")
                    viewModel.loadNextPage()
                }
            }
        })

        blog_post_recyclerview.adapter = recyclerAdapter
    }

    override fun onBlogSelected(itemPosition: Int) {
        recyclerAdapter.findBlogPost(itemPosition).let{
            viewModel.setBlogPost(it)
            findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search_menu, menu)

        activity?.let {
            val searchManager: SearchManager = it.getSystemService(SEARCH_SERVICE) as SearchManager
            searchView = menu.findItem(R.id.action_search).actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(it.componentName))
            searchView.maxWidth = Integer.MAX_VALUE
            searchView.setIconifiedByDefault(true)
            searchView.isSubmitButtonEnabled = true

            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{

                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.e(TAG, "onQueryTextSubmit: ${query}")
                    viewModel.loadFirstPage(query)
                    onQuerySubmitted()
                    blog_post_recyclerview.smoothScrollToPosition(0)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })

            val searchPlate = searchView.findViewById(R.id.search_src_text) as EditText
            searchPlate.setOnEditorActionListener { v, actionId, event ->

                Log.d(TAG, "SearchPlate: clicked")

                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_SEARCH ) {
                    val searchQuery = v.text.toString()
                    Log.e(TAG, "SearchPlate: executing search...: ${searchQuery}")
                    if(searchQuery.isBlank()){
                        viewModel.loadFirstPage("")
                        onQuerySubmitted()
                    }
                    else{
                        searchView.setQuery(searchQuery, true)
                    }
                    blog_post_recyclerview.smoothScrollToPosition(0)
                }
                true
            }

            val searchButton = searchView.findViewById(R.id.search_go_btn) as View
            searchButton.setOnClickListener {
                val searchQuery = searchPlate.text.toString()
                Log.e(TAG, "SearchButton: executing search...: ${searchQuery}")
                if(searchQuery.isBlank()){
                    viewModel.loadFirstPage("")
                    onQuerySubmitted()
                }
                else{
                    searchView.setQuery(searchQuery, true)
                }
                blog_post_recyclerview.smoothScrollToPosition(0)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_filter_settings -> {
                showFilterDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showFilterDialog(){

        activity?.let {
            val dialog = MaterialDialog(it)
                .noAutoDismiss()
                .customView(R.layout.layout_blog_filter)

            val view = dialog.getCustomView()

            val filter = sharedPreferences.getString(BLOG_FILTER, BLOG_FILTER_DATE_UPDATED)
            val order = sharedPreferences.getString(BLOG_ORDER, BLOG_ORDER_ASC)

            if(filter.equals(BLOG_FILTER_DATE_UPDATED)){
                view.findViewById<RadioGroup>(R.id.filter_group).check(R.id.filter_date)
            }
            else{
                view.findViewById<RadioGroup>(R.id.filter_group).check(R.id.filter_author)
            }

            if(order.equals(BLOG_ORDER_ASC)){
                view.findViewById<RadioGroup>(R.id.order_group).check(R.id.filter_asc)
            }
            else{
                view.findViewById<RadioGroup>(R.id.order_group).check(R.id.filter_desc)
            }

            view.findViewById<TextView>(R.id.positive_button).setOnClickListener{
                Log.d(TAG, "FilterDialog: apply filter.")

                val selectedFilter = dialog.getCustomView().findViewById<RadioButton>(
                    dialog.getCustomView().findViewById<RadioGroup>(R.id.filter_group).checkedRadioButtonId
                )
                val selectedOrder= dialog.getCustomView().findViewById<RadioButton>(
                    dialog.getCustomView().findViewById<RadioGroup>(R.id.order_group).checkedRadioButtonId
                )

                var filter = BLOG_FILTER_DATE_UPDATED
                if(selectedFilter.text.toString().equals(getString(R.string.filter_author))){
                    filter = BLOG_FILTER_USERNAME
                }

                var order = ""
                if(selectedOrder.text.toString().equals(getString(R.string.filter_desc))){
                    order = "-"
                }
                applyFilterOptions(
                    filter,
                    order
                )
                dialog.dismiss()
            }

            view.findViewById<TextView>(R.id.negative_button).setOnClickListener {
                Log.d(TAG, "FilterDialog: cancelling filter.")
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    fun applyFilterOptions(filter: String, order: String){
        Log.d(TAG, "applyFilterOptions: $filter, $order")
        editor.putString(BLOG_FILTER, filter)
        editor.apply()


        editor.putString(BLOG_ORDER, order)
        editor.apply()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

        sharedPreferences?.let{
            when(key){

                BLOG_FILTER ->{
                    viewModel.setBlogFilter(sharedPreferences.getString(key, BLOG_FILTER_DATE_UPDATED))
                    onBlogFilterEvent()
                }

                BLOG_ORDER ->{
                    viewModel.setBlogOrder(sharedPreferences.getString(key, BLOG_ORDER_ASC))
                    onBlogFilterEvent()
                }
                else -> return
            }
        }
    }

    fun onBlogFilterEvent(){
        viewModel.setStateEvent(BlogSearchEvent())
        blog_post_recyclerview.smoothScrollToPosition(0)
    }

    fun onQuerySubmitted(){
        stateChangeListener.hideSoftKeyboard()
        focusable_view.requestFocus()
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onRefresh() {
        onBlogFilterEvent()
        swipe_refresh.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        blog_post_recyclerview.adapter = null
    }
}




















