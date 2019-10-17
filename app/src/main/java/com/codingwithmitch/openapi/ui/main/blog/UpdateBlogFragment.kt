package com.codingwithmitch.openapi.ui.main.blog

import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent
import kotlinx.android.synthetic.main.fragment_update_blog.*
import okhttp3.MultipartBody

class UpdateBlogFragment : BaseBlogFragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
    }

    fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState.data?.let{ data ->
                data.data?.getContentIfNotHandled()?.let{ viewState ->

                    // if this is not null, the blogpost was updated
                    viewState.viewBlogFields.blogPost?.let{ blogPost ->
                        // TODO("onBlogPostUpdateSuccess")
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.updatedBlogFields.let{ updatedBlogFields ->
                setBlogProperties(
                    updatedBlogFields.updatedBlogTitle,
                    updatedBlogFields.updatedBlogBody,
                    updatedBlogFields.updatedImageUri
                )
            }
        })
    }

    fun setBlogProperties(title: String?, body: String?, image: Uri?){
        requestManager
            .load(image)
            .into(blog_image)
        blog_title.setText(title)
        blog_body.setText(body)
    }

    private fun saveChanges(){
        var multipartBody: MultipartBody.Part? = null
        viewModel.setStateEvent(
            BlogStateEvent.UpdateBlogPostEvent(
                blog_title.text.toString(),
                blog_body.text.toString(),
                multipartBody
            )
        )
        stateChangeListener.hideSoftKeyboard()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save -> {
                saveChanges()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}


















