package com.codingwithmitch.openapi.ui.main.blog

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.BlogPost
import kotlinx.android.synthetic.main.fragment_view_blog.blog_body
import kotlinx.android.synthetic.main.fragment_view_blog.blog_image
import kotlinx.android.synthetic.main.fragment_view_blog.blog_title
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.codingwithmitch.openapi.ui.*
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent
import com.codingwithmitch.openapi.util.Constants.Companion.CROP_IMAGE_INTENT_CODE
import com.codingwithmitch.openapi.util.Constants.Companion.GALLERY_REQUEST_CODE
import com.codingwithmitch.openapi.util.FileUtil
import kotlinx.android.synthetic.main.fragment_update_blog.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*


class UpdateBlogFragment : BaseBlogFragment() {

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

        image_container.setOnClickListener {
            if(stateChangeListener.isStoragePermissionGranted()){
                pickFromGallery()
            }
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun launchCropIntent(uri: Uri){
        val cropIntent = Intent("com.android.camera.action.CROP")

        cropIntent.setDataAndType(uri, "image/*")

        cropIntent.putExtra("crop", "true")
        cropIntent.putExtra("aspectX", 16)
        cropIntent.putExtra("aspectY", 9)
        cropIntent.putExtra("return-data", true)
        cropIntent.putExtra("scale", true)

        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(cropIntent, CROP_IMAGE_INTENT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                GALLERY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        activity?.let{
                            launchCropIntent(uri)
                        }
                    }?: showImageSelectionError()
                }

                CROP_IMAGE_INTENT_CODE -> {
                    data?.data?.let { uri ->
                        viewModel.setUpdatedBlogFields(
                            title = null,
                            body = null,
                            uri = uri
                        )
                    } ?: showImageSelectionError()
                }
            }
        }
    }

    fun showImageSelectionError(){
        stateChangeListener.onDataStateChange(
            DataState(
                Event(StateError(Response("Something went wrong with the image.", ResponseType.Dialog()))),
                Loading(isLoading = false),
                Data(Event.dataEvent(null), null)
            )
        )
    }

    fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState.data?.let{ data ->
                data.data?.getContentIfNotHandled()?.let{ viewState ->
                    viewState.blogPost?.let{ blogPost ->
                        viewModel.setBlogPost(blogPost)
                        viewModel.updateListItem(blogPost)
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
        blog_title.text = title
        blog_body.text = body
    }

    private fun saveChanges(){
        var multipartBody: MultipartBody.Part? = null
        viewModel.viewState.value?.updatedBlogFields?.updatedImageUri?.let{ imageUri ->
            imageUri.path?.let{filePath ->
                view?.context?.let{ context ->
                    FileUtil.getUriRealPathAboveKitkat(context, imageUri)?.let{
                        val imageFile = File(it)
                        Log.d(TAG, "UpdateBlogFragment, imageFile: file: ${imageFile}")
                        val requestBody =
                            RequestBody.create(
                                MediaType.parse(context.contentResolver.getType(imageUri)),
                                imageFile
                            )
                        // name = field name in serializer
                        // filename = name of the image file
                        // requestBody = file with file type information
                        multipartBody = MultipartBody.Part.createFormData(
                            "image",
                            imageFile.name,
                            requestBody
                        )
                    }
                }
            }
        }

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

    override fun onPause() {
        super.onPause()
        viewModel.setUpdatedBlogFields(
            uri = null,
            title = blog_title.text.toString(),
            body = blog_body.text.toString()
        )
    }

}




































