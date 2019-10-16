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
import com.codingwithmitch.openapi.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_blog.*
import javax.inject.Inject
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.repository.main.BlogQueryUtils.Companion.BLOG_FILTER_DATE_UPDATED
import com.codingwithmitch.openapi.repository.main.BlogQueryUtils.Companion.BLOG_FILTER_USERNAME
import com.codingwithmitch.openapi.repository.main.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.codingwithmitch.openapi.ui.main.blog.state.*
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent.*
import com.codingwithmitch.openapi.util.PreferenceKeys.Companion.BLOG_FILTER
import com.codingwithmitch.openapi.util.PreferenceKeys.Companion.BLOG_ORDER

class BlogFragment : BaseBlogFragment(),
    BlogListAdapter.BlogViewHolder.Interaction,
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

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            if(dataState != null){
                handlePagination(dataState)
                stateChangeListener.onDataStateChange(dataState)
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
                    viewModel.setStateEvent(NextPageEvent())
                }
            }
        })

        blog_post_recyclerview.adapter = recyclerAdapter
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search_menu, menu)
        initSearchView(menu)

        // QUERY SUBMITTED
//        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
//
//            override fun onQueryTextSubmit(query: String): Boolean {
//                Log.e(TAG, "SearchView: onQueryTextSubmit: ${query}")
//                viewModel.loadFirstPage().let {
//                    onQuerySubmitted()
//                }
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return false
//            }
//
//        })

        // ENTER ON COMPUTER KEYBOARD OR ARROW ON VIRTUAL KEYBOARD
        val searchPlate = searchView.findViewById(R.id.search_src_text) as EditText
        searchPlate.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_SEARCH ) {
                val searchQuery = v.text.toString()
                Log.e(TAG, "SearchView: (keyboard or arrow) executing search...: ${searchQuery}")
                viewModel.setQuery(searchQuery).let{
                    onBlogSearchOrFilter()
                }
            }
            true
        }

        // SEARCH BUTTON CLICKED
        val searchButton = searchView.findViewById(R.id.search_go_btn) as View
        searchButton.setOnClickListener {
            val searchQuery = searchPlate.text.toString()
            Log.e(TAG, "SearchView: (button) executing search...: ${searchQuery}")
            viewModel.setQuery(searchQuery).let {
                onBlogSearchOrFilter()
            }

        }
    }

    fun onBlogSearchOrFilter(){
        blog_post_recyclerview.smoothScrollToPosition(0)


//        if(viewModel.viewState.value!!.blogFields.searchQuery.isBlank()){
//            viewModel.loadFirstPage().let {
//                onQuerySubmitted()
//            }
//        }
//        else{
//            searchView.setQuery(
//                viewModel.viewState.value!!.blogFields.searchQuery,
//                true
//            )
//        }


        viewModel.loadFirstPage().let {
            onQuerySubmitted()
        }
    }


    private fun initSearchView(menu: Menu){
        activity?.apply {
            val searchManager: SearchManager = getSystemService(SEARCH_SERVICE) as SearchManager
            searchView = menu.findItem(R.id.action_search).actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.maxWidth = java.lang.Integer.MAX_VALUE
            searchView.setIconifiedByDefault(true)
            searchView.isSubmitButtonEnabled = true
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
        onBlogSearchOrFilter()
        swipe_refresh.isRefreshing = false
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
        recyclerAdapter.findBlogPost(position).let{
            viewModel.setBlogPost(it)
            findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        blog_post_recyclerview.adapter = null
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
                    onBlogSearchOrFilter()
                }

                BLOG_ORDER ->{
                    onBlogSearchOrFilter()
                }
                else -> return
            }
        }
    }
}




















