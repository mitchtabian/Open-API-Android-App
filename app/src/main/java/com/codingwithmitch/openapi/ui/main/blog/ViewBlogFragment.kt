package com.codingwithmitch.openapi.ui.main.blog


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.api.load

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent.*
import com.codingwithmitch.openapi.util.DateUtils
import kotlinx.android.synthetic.main.fragment_view_blog.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ViewBlogFragment : BaseBlogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
        viewModel.setStateEvent(CheckAuthorOfBlogPost())
    }

    fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState.data?.let{ data ->
                data.data?.getContentIfNotHandled()?.let{ viewState ->
                    viewState.accountProperties?.let{ accountProperties ->
                        viewModel.setAccountProperties(accountProperties)
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.blogPost?.let{ blogPost ->
                setBlogProperties(blogPost)
            }

            viewState.accountProperties?.let{ accountProperties ->
                if(viewModel.isAuthorOfBlogPost()){
                    activity?.invalidateOptionsMenu()
                }
            }
        })
    }

    fun setBlogProperties(blogPost: BlogPost){
        GlobalScope.launch(Main){
//            blog_image.load(blogPost.image, imageLoader)
            blog_image.load(blogPost.image)
            blog_title.setText(blogPost.title)
            blog_author.setText(blogPost.username)
            blog_update_date.setText(DateUtils.convertLongToStringDate(blogPost.date_updated))
            blog_body.setText(blogPost.body)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if(viewModel.isAuthorOfBlogPost()){
            inflater.inflate(R.menu.edit_view_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(viewModel.isAuthorOfBlogPost()){
            when(item.itemId){
                R.id.edit -> {
                    findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

}






















