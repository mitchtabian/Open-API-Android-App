package com.codingwithmitch.openapi.presentation.main.blog.detail

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.business.domain.models.BlogPost
import com.codingwithmitch.openapi.business.domain.util.*
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling.Companion.ERROR_BLOG_DOES_NOT_EXIST
import com.codingwithmitch.openapi.databinding.FragmentViewBlogBinding
import com.codingwithmitch.openapi.presentation.main.blog.BaseBlogFragment
import com.codingwithmitch.openapi.presentation.util.processQueue

class ViewBlogFragment : BaseBlogFragment()
{
    private val requestOptions = RequestOptions
        .placeholderOf(R.drawable.default_image)
        .error(R.drawable.default_image)

    private val viewModel: ViewBlogViewModel by viewModels()

    private var _binding: FragmentViewBlogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewBlogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
        uiCommunicationListener.expandAppBar()

        binding.deleteButton.setOnClickListener {
            viewModel.onTriggerEvent(ViewBlogEvents.DeleteBlog)
        }

        // If an update occurred from UpdateBlogFragment, refresh the BlogPost
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(SHOULD_REFRESH)?.observe(viewLifecycleOwner) { shouldRefresh ->
            shouldRefresh?.run {
                viewModel.onTriggerEvent(ViewBlogEvents.Refresh)
                findNavController().currentBackStackEntry?.savedStateHandle?.set(SHOULD_REFRESH, null)
            }
        }
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            if(state.queue.peek()?.response?.message == ERROR_BLOG_DOES_NOT_EXIST){
                 findNavController().popBackStack(R.id.blogFragment, false)
            }else{
                processQueue(
                    context = context,
                    queue = state.queue,
                    stateMessageCallback = object: StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.onTriggerEvent(ViewBlogEvents.OnRemoveHeadFromQueue)
                        }
                    })
            }

            state.blogPost?.let { setBlogProperties(it) }

            if(state.isAuthor == true){
                adaptViewToAuthorMode()
            }

            if(state.isDeleteComplete){
                findNavController().popBackStack(R.id.blogFragment, false)
            }
        })
    }

    private fun adaptViewToAuthorMode(){
        activity?.invalidateOptionsMenu()
        binding.deleteButton.visibility = View.VISIBLE
    }

    private fun setBlogProperties(blogPost: BlogPost){
        Glide.with(this)
            .setDefaultRequestOptions(requestOptions)
            .load(blogPost.image)
            .into(binding.blogImage)
        binding.blogTitle.setText(blogPost.title)
        binding.blogAuthor.setText(blogPost.username)
        binding.blogUpdateDate.setText(DateUtils.convertLongToStringDate(blogPost.dateUpdated))
        binding.blogBody.setText(blogPost.body)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}







