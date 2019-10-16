package com.codingwithmitch.openapi.ui.main.blog

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent.*
import com.codingwithmitch.openapi.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_blog.*
import javax.inject.Inject

class BlogFragment : BaseBlogFragment(),
    BlogListAdapter.Interaction
{

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
        setHasOptionsMenu(true)

        initRecyclerView()
        subscribeObservers()
        executeSearch()
    }

    private fun executeSearch(){
        viewModel.setQuery("")
        viewModel.setStateEvent(BlogSearchEvent())
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            if(dataState != null){
                stateChangeListener.onDataStateChange(dataState)
                dataState.data?.let {
                    it.data?.let{
                        it.getContentIfNotHandled()?.let{
                            Log.d(TAG, "BlogFragment, DataState: ${it}")
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
                    blogList = viewState.blogFields.blogList,
                    isQueryExhausted = true
                )
            }
        })
    }

    private fun initRecyclerView(){

        blog_post_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@BlogFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = BlogListAdapter(requestManager,  this@BlogFragment)
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == recyclerAdapter.itemCount.minus(1)) {
                        Log.d(TAG, "BlogFragment: attempting to load next page...")
//                    TODO("load next page using ViewModel")
                    }
                }
            })
            adapter = recyclerAdapter
        }

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
}

















