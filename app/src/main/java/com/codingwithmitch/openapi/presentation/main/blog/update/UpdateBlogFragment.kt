package com.codingwithmitch.openapi.presentation.main.blog.update

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.business.domain.util.*
import com.codingwithmitch.openapi.business.domain.util.Constants.Companion.GALLERY_REQUEST_CODE
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling.Companion.SOMETHING_WRONG_WITH_IMAGE
import com.codingwithmitch.openapi.databinding.FragmentUpdateBlogBinding
import com.codingwithmitch.openapi.presentation.main.blog.BaseBlogFragment
import com.codingwithmitch.openapi.presentation.util.processQueue
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class UpdateBlogFragment : BaseBlogFragment()
{

    private val requestOptions = RequestOptions
        .placeholderOf(R.drawable.default_image)
        .error(R.drawable.default_image)

    private val viewModel: UpdateBlogViewModel by viewModels()

    private var _binding: FragmentUpdateBlogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBlogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()

        binding.imageContainer.setOnClickListener {
            if(uiCommunicationListener.isStoragePermissionGranted()){
                pickFromGallery()
            }
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun launchImageCrop(uri: Uri){
        context?.let{
            CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(it, this)
        }
    }

    private fun showImageSelectionError(){
        viewModel.onTriggerEvent(UpdateBlogEvents.Error(
            stateMessage = StateMessage(
                response = Response(
                    message = SOMETHING_WRONG_WITH_IMAGE,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            )
        ))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                GALLERY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        activity?.let{
                            launchImageCrop(uri)
                        }
                    }?: showImageSelectionError()
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    Log.d(TAG, "CROP: CROP_IMAGE_ACTIVITY_REQUEST_CODE")
                    val result = CropImage.getActivityResult(data)
                    val resultUri = result.uri
                    Log.d(TAG, "CROP: CROP_IMAGE_ACTIVITY_REQUEST_CODE: uri: ${resultUri}")
                    viewModel.onTriggerEvent(UpdateBlogEvents.OnUpdateUri(resultUri))
                }

                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    Log.d(TAG, "CROP: ERROR")
                    showImageSelectionError()
                }
            }
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
                        viewModel.onTriggerEvent(UpdateBlogEvents.OnRemoveHeadFromQueue)
                    }
                })
            state.blogPost?.let { blogPost ->
                setBlogProperties(
                    blogPost.title,
                    blogPost.body,
                    blogPost.image.toUri()
                )
            }
            if(state.isUpdateComplete){
                findNavController().popBackStack(R.id.viewBlogFragment, false)
            }
        })
    }

    fun setBlogProperties(title: String?, body: String?, image: Uri?){
        image?.let {
            Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(it)
                .into(binding.blogImage)
        }
        binding.blogTitle.setText(title)
        binding.blogBody.setText(body)
    }

    private fun saveChanges(){
        cacheState()
        viewModel.onTriggerEvent(UpdateBlogEvents.Update)
        uiCommunicationListener.hideSoftKeyboard()
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

    private fun cacheState(){
        val title = binding.blogTitle.text.toString()
        val body = binding.blogBody.text.toString()
        viewModel.onTriggerEvent(UpdateBlogEvents.OnUpdateTitle(title))
        viewModel.onTriggerEvent(UpdateBlogEvents.OnUpdateBody(body))
    }

    override fun onPause() {
        super.onPause()
        cacheState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}










