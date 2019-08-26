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

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.groupie.Items.BlogItem
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.groupie.decoration.BottomSpacingItemDecoration
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_blog.*


class BlogFragment : BaseBlogFragment() {


    private lateinit var searchView: SearchView
    private var groupAdapter: GroupAdapter<ViewHolder>? = null

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
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            stateChangeListener.onDataStateChange(dataState)
            if(dataState != null){
                dataState.data?.let {
                    it.data?.let{
                        it.getContentIfNotHandled()?.let{
                            Log.d(TAG, "BlogFragment, DataState: ${it}")
                            viewModel.setBlogListData(it.blogList)
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer{viewState ->
            if(viewState != null){
                groupAdapter?.updateAsync(viewState.blogList.toGroupieList())
            }
        })
    }

    private fun List<BlogPost>.toGroupieList(): List<BlogItem>{
        return this.map {
            BlogItem(it)
        }
    }

    private fun initRecyclerView(){
        blog_post_recyclerview?.apply {
            groupAdapter = GroupAdapter<ViewHolder>()
            layoutManager = LinearLayoutManager(this@BlogFragment.context)
            val carouselDecoration = BottomSpacingItemDecoration(30)
            removeItemDecoration(carouselDecoration) // does nothing if not applied already
            addItemDecoration(carouselDecoration)
            adapter = groupAdapter
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

                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let{
                        Log.d(TAG, "onQueryTextSubmit: ${query}")
                        viewModel.setStateEvent(BlogStateEvent.BlogSearchEvent(query))
                        stateChangeListener.hideSoftKeyboard()
                        focusable_view.requestFocus()
                        return true
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        groupAdapter = null
    }
}















