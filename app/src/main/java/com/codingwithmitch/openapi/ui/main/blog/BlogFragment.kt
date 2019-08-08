package com.codingwithmitch.openapi.ui.main.blog


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.repository.main.BlogQueryUtils.Companion.ORDER_BY_DESC_DATE_UPDATED


class BlogFragment : BaseBlogFragment() {

    var list: List<BlogPost>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()
        viewModel.searchBlogPosts("", ORDER_BY_DESC_DATE_UPDATED, 1)
    }

    private fun subscribeObservers(){
        viewModel.observeDataState().observe(viewLifecycleOwner, Observer {

            it.blogPostList?.let {
                if(it != list){
                    list = it
                    for(blogPost in it){
                        Log.d(TAG, "blog post: ${blogPost.title}")
                    }
                }

            }
        })
    }
}













