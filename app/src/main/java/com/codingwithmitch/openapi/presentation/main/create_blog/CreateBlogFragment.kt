package com.codingwithmitch.openapi.presentation.main.create_blog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.business.domain.util.*
import com.codingwithmitch.openapi.databinding.FragmentCreateBlogBinding
import com.codingwithmitch.openapi.presentation.util.processQueue

class CreateBlogFragment : BaseCreateBlogFragment() {

    private val requestOptions = RequestOptions
        .placeholderOf(R.drawable.default_image)
        .error(R.drawable.default_image)

    private val viewModel: CreateBlogViewModel by viewModels()

    private var _binding: FragmentCreateBlogBinding? = null
    private val binding get() = _binding!!

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage
                .activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uriContent
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
                viewModel.onTriggerEvent(CreateBlogEvents.OnUpdateUri(uri))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBlogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        binding.blogImage.setOnClickListener {
            if (uiCommunicationListener.isStoragePermissionGranted()) {
                cropActivityResultLauncher.launch(null)
            }
        }

        binding.updateTextview.setOnClickListener {
            if (uiCommunicationListener.isStoragePermissionGranted()) {
                cropActivityResultLauncher.launch(null)
            }
        }

        subscribeObservers()
    }

    fun subscribeObservers() {
        viewModel.state.observe(viewLifecycleOwner, { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)
            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(CreateBlogEvents.OnRemoveHeadFromQueue)
                    }
                })
            setBlogProperties(
                title = state.title,
                body = state.body,
                uri = state.uri,
            )
            if (state.onPublishSuccess) {
                findNavController().popBackStack(R.id.blogFragment, false)
            }
        })
    }

    private fun setBlogProperties(
        title: String,
        body: String,
        uri: Uri?
    ) {
        if (uri != null) {
            Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(uri)
                .into(binding.blogImage)
        } else {
            Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(R.drawable.default_image)
                .into(binding.blogImage)
        }

        binding.blogTitle.setText(title)
        binding.blogBody.setText(body)
    }

    private fun cacheState() {
        val title = binding.blogTitle.text.toString()
        val body = binding.blogBody.text.toString()
        viewModel.onTriggerEvent(CreateBlogEvents.OnUpdateTitle(title))
        viewModel.onTriggerEvent(CreateBlogEvents.OnUpdateBody(body))
    }

    override fun onPause() {
        super.onPause()
        cacheState()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.publish_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.publish -> {
                cacheState()
                viewModel.onTriggerEvent(CreateBlogEvents.PublishBlog)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}










