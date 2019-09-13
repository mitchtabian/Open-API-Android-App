package com.codingwithmitch.openapi.ui.main.create_blog


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.*
import com.codingwithmitch.openapi.ui.main.create_blog.state.CreateBlogStateEvent
import com.codingwithmitch.openapi.util.Constants.Companion.CROP_IMAGE_INTENT_CODE
import com.codingwithmitch.openapi.util.Constants.Companion.GALLERY_REQUEST_CODE
import com.codingwithmitch.openapi.util.ErrorHandling.NetworkErrors.Companion.ERROR_MUST_SELECT_IMAGE
import com.codingwithmitch.openapi.util.ErrorHandling.NetworkErrors.Companion.ERROR_SOMETHING_WRONG_WITH_IMAGE
import com.codingwithmitch.openapi.util.FileUtil
import com.codingwithmitch.openapi.util.SuccessHandling.NetworkSuccessResponses.Companion.SUCCESS_BLOG_CREATED
import kotlinx.android.synthetic.main.fragment_create_blog.blog_body
import kotlinx.android.synthetic.main.fragment_create_blog.blog_image
import kotlinx.android.synthetic.main.fragment_create_blog.blog_title
import kotlinx.android.synthetic.main.fragment_create_blog.image_container
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class CreateBlogFragment : BaseCreateFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // setup back navigation for this graph
        setupActionBarWithNavController(R.id.createBlogFragment, activity as AppCompatActivity)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_blog, container, false)
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
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
                    }?: showErrorDialog(ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }

                CROP_IMAGE_INTENT_CODE -> {
                    data?.data?.let { uri ->
                        viewModel.setNewBlogFields(
                            title = null,
                            body = null,
                            uri = uri
                        )
                    } ?: showErrorDialog(ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }
            }
        }
    }

    fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState.data?.let{ data ->
               data.response?.let{ event ->
                   event.peekContent().let { response ->
                       response.message?.let{ message ->
                           if(message.equals(SUCCESS_BLOG_CREATED)){
                               viewModel.clearNewBlogFields()
                           }
                       }
                   }
               }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.blogFields.let{ newBlogFields ->
                setBlogProperties(
                    newBlogFields.newBlogTitle,
                    newBlogFields.newBlogBody,
                    newBlogFields.newImageUri
                )
            }
        })
    }

    fun setBlogProperties(title: String?, body: String?, image: Uri?){
        if(image != null){
            requestManager
                .load(image)
                .into(blog_image)
        }
        else{
            requestManager
                .load(R.drawable.default_image)
                .into(blog_image)
        }

        blog_title.setText(title)
        blog_body.setText(body)
    }

    private fun publishNewBlog(){
        var multipartBody: MultipartBody.Part? = null
        viewModel.viewState.value?.blogFields?.newImageUri?.let{ imageUri ->
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

        multipartBody?.let {

            viewModel.setStateEvent(
                CreateBlogStateEvent.CreateNewBlogEvent(
                    blog_title.text.toString(),
                    blog_body.text.toString(),
                    it
                )
            )
            stateChangeListener.hideSoftKeyboard()
        }?: showErrorDialog(ERROR_MUST_SELECT_IMAGE)

    }

    fun showErrorDialog(errorMessage: String){
        stateChangeListener.onDataStateChange(
            DataState(
                Event(StateError(Response(errorMessage, ResponseType.Dialog()))),
                Loading(isLoading = false),
                Data(Event.dataEvent(null), null)
            )
        )
    }

    override fun onPause() {
        super.onPause()
        viewModel.setNewBlogFields(
            blog_title.text.toString(),
            blog_body.text.toString(),
            null
        )
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.publish_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.publish -> {
                val callback: BaseActivity.AreYouSureCallback = object: BaseActivity.AreYouSureCallback {

                    override fun proceed() {
                        publishNewBlog()
                    }

                    override fun cancel() {
                        // ignore
                    }

                }
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        getString(R.string.are_you_sure_publish),
                        UIMessageType.AreYouSureDialog(callback)
                    )
                )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}























