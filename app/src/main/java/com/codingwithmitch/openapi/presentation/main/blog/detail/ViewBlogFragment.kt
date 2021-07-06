package com.codingwithmitch.openapi.presentation.main.blog.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.business.domain.models.BlogPost
import com.codingwithmitch.openapi.business.domain.util.*
import com.codingwithmitch.openapi.presentation.main.blog.BaseBlogFragment
import com.codingwithmitch.openapi.presentation.util.processQueue
import kotlinx.android.synthetic.main.fragment_view_blog.*

class ViewBlogFragment : BaseBlogFragment(R.layout.fragment_view_blog)
{
    private val requestOptions = RequestOptions
        .placeholderOf(R.drawable.default_image)
        .error(R.drawable.default_image)

    private val viewModel: ViewBlogViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
        uiCommunicationListener.expandAppBar()

        delete_button.setOnClickListener {
            viewModel.onTriggerEvent(ViewBlogEvents.DeleteBlog)
        }
    }

    fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(ViewBlogEvents.OnRemoveHeadFromQueue)
                    }
                })

            state.blogPost?.let { setBlogProperties(it) }

            if(state.isAuthor == true){
                adaptViewToAuthorMode()
            }

            if(state.isDeleteComplete){
                findNavController().popBackStack(R.id.blogFragment, false)
            }
        })
    }

    fun adaptViewToAuthorMode(){
        activity?.invalidateOptionsMenu()
        delete_button.visibility = View.VISIBLE
    }

    fun setBlogProperties(blogPost: BlogPost){
        Glide.with(this)
            .setDefaultRequestOptions(requestOptions)
            .load(blogPost.image)
            .into(blog_image)
        blog_title.setText(blogPost.title)
        blog_author.setText(blogPost.username)
        blog_update_date.setText(DateUtils.convertLongToStringDate(blogPost.date_updated))
        blog_body.setText(blogPost.body)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if(viewModel.state.value?.isAuthor == true){
            inflater.inflate(R.menu.edit_view_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(viewModel.state.value?.isAuthor == true){
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
        try{
            viewModel.state.value?.let { state ->
                state.blogPost?.let { blogPost ->
                    val bundle = bundleOf("blogPostPk" to blogPost.pk)
                    findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment, bundle)
                } ?: throw Exception("Null BlogPost")
            }?: throw Exception("Null BlogPost")
        }catch (e: Exception){
            e.printStackTrace()
            viewModel.onTriggerEvent(ViewBlogEvents.Error(
                stateMessage = StateMessage(
                    response = Response(
                        message = e.message,
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    )
                )
            ))
        }
    }
}







