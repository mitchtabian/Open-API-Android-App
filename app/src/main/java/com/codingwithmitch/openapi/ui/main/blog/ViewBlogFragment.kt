package com.codingwithmitch.openapi.ui.main.blog

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.ui.AreYouSureCallback
import com.codingwithmitch.openapi.ui.UIMessage
import com.codingwithmitch.openapi.ui.UIMessageType
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent.*
import com.codingwithmitch.openapi.ui.main.blog.viewmodel.isAuthorOfBlogPost
import com.codingwithmitch.openapi.ui.main.blog.viewmodel.removeDeletedBlogPost
import com.codingwithmitch.openapi.ui.main.blog.viewmodel.setIsAuthorOfBlogPost
import com.codingwithmitch.openapi.util.DateUtils
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
import kotlinx.android.synthetic.main.fragment_view_blog.*

class ViewBlogFragment : BaseBlogFragment(){


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
        checkIsAuthorOfBlogPost()
        stateChangeListener.expandAppBar()

        delete_button.setOnClickListener {
            confirmDeleteRequest()
        }
    }

    fun confirmDeleteRequest(){
        val callback: AreYouSureCallback = object: AreYouSureCallback {

            override fun proceed() {
                deleteBlogPost()
            }

            override fun cancel() {
                // ignore
            }

        }
        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                getString(R.string.are_you_sure_delete),
                UIMessageType.AreYouSureDialog(callback)
            )
        )
    }

    fun deleteBlogPost(){
        viewModel.setStateEvent(
            DeleteBlogPostEvent()
        )
    }

    fun checkIsAuthorOfBlogPost(){
        viewModel.setIsAuthorOfBlogPost(false) // reset
        viewModel.setStateEvent(CheckAuthorOfBlogPost())
    }

    fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            stateChangeListener.onDataStateChange(dataState)

            dataState.data?.let { data ->
                data.data?.getContentIfNotHandled()?.let { viewState ->
                    viewModel.setIsAuthorOfBlogPost(
                        viewState.viewBlogFields.isAuthorOfBlogPost
                    )
                }
                data.response?.peekContent()?.let{ response ->
                    if(response.message.equals(SUCCESS_BLOG_DELETED)){
                        viewModel.removeDeletedBlogPost()
                        findNavController().popBackStack()
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.viewBlogFields.blogPost?.let{ blogPost ->
                setBlogProperties(blogPost)
            }

            if(viewState.viewBlogFields.isAuthorOfBlogPost){
                adaptViewToAuthorMode()
            }
        })
    }

    fun adaptViewToAuthorMode(){
        activity?.invalidateOptionsMenu()
        delete_button.visibility = View.VISIBLE
    }

    fun setBlogProperties(blogPost: BlogPost){
        requestManager
            .load(blogPost.image)
            .into(blog_image)
        blog_title.setText(blogPost.title)
        blog_author.setText(blogPost.username)
        blog_update_date.setText(DateUtils.convertLongToStringDate(blogPost.date_updated))
        blog_body.setText(blogPost.body)
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
                    navUpdateBlogFragment()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navUpdateBlogFragment(){
        findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
    }
}













