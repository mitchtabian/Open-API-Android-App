package com.codingwithmitch.openapi.ui.main.blog


import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.main.blog.BlogRecyclerAdapter.BlogViewHolder.*
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState
import com.codingwithmitch.openapi.util.TopSpacingItemDecoration
import com.codingwithmitch.openapi.util.ErrorHandling
import kotlinx.android.synthetic.main.fragment_blog.*
import javax.inject.Inject
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import android.widget.EditText




class BlogFragment : BaseBlogFragment(), BlogClickListener {

    @Inject
    lateinit var imageLoader: ImageLoader

    private lateinit var searchView: SearchView
    private var recyclerAdapter: BlogRecyclerAdapter? = null

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
        setHasOptionsMenu(true)
        initRecyclerView()
        subscribeObservers()
        viewModel.loadInitialBlogs()
    }

    fun checkPaginationEnd(dataState: DataState<BlogViewState>?){
        dataState?.let{
            it.error?.let{event ->
                event.peekContent().response.message?.let{
                    if(ErrorHandling.NetworkErrors.isPaginationDone(it)){

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
                            Log.d(TAG, "BlogFragment, DataState: isQueryInProgress?: ${it.isQueryInProgress}")
                            viewModel.setQueryInProgress(it.isQueryInProgress)
                            viewModel.setBlogListData(it.blogList)
                        }
                    }
                }
                dataState.error?.let{
                    it.peekContent().let {
                        Log.d(TAG, "BlogFragment, ErrorState: ${it}")
                        viewModel.setQueryExhausted(ErrorHandling.NetworkErrors.isPaginationDone(it.response.message))
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer{ viewState ->
            Log.d(TAG, "BlogFragment, ViewState: ${viewState}")
            if(viewState != null){
                recyclerAdapter?.submitList(viewState.blogList)
                if(viewState.isQueryExhausted){
                    recyclerAdapter?.setNoMoreResults()
                }
            }
        })
    }


    private fun initRecyclerView(){
        blog_post_recyclerview?.apply {
            layoutManager = LinearLayoutManager(this@BlogFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = BlogRecyclerAdapter(imageLoader, this@BlogFragment)
            adapter = recyclerAdapter

            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == adapter?.itemCount?.minus(1)) {
                        Log.d(TAG, "BlogFragment: attempting to load next page...")
                        viewModel.loadNextPage()
                    }
                }
            })
        }
    }

    override fun onBlogSelected(itemPosition: Int) {
        Log.d(TAG, "BlogFragment, onBlogSelected: ${itemPosition}")
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
                    stateChangeListener.hideSoftKeyboard()
                    focusable_view.requestFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })

            val searchPlate = searchView.findViewById(R.id.search_src_text) as EditText
            searchPlate.setOnEditorActionListener { v, actionId, event ->

                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    val searchQuery = v.text.toString()
                    Log.e(TAG, "SearchPlate: executing search...: ${searchQuery}")
                    if(searchQuery.isBlank()){
                        viewModel.loadFirstPage("")
                    }
                    else{
                        searchView.setQuery(searchQuery, true)
                    }
                }
                true
            }

            val searchButton = searchView.findViewById(R.id.search_go_btn) as View
            searchButton.setOnClickListener {
                val searchQuery = searchPlate.text.toString()
                Log.e(TAG, "SearchButton: executing search...: ${searchQuery}")
                if(searchQuery.isBlank()){
                    viewModel.loadFirstPage("")
                }
                else{
                    searchView.setQuery(searchQuery, true)
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        recyclerAdapter = null
    }
}




















