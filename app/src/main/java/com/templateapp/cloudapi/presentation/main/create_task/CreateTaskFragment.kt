package com.templateapp.cloudapi.presentation.main.create_task

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
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.databinding.FragmentCreateTaskBinding
import com.templateapp.cloudapi.presentation.util.processQueue

class CreateTaskFragment : BaseCreateTaskFragment() {

    private val requestOptions = RequestOptions
        .placeholderOf(R.drawable.default_white)
        .error(R.drawable.default_white)

    private val viewModel: CreateTaskViewModel by viewModels()

    private var _binding: FragmentCreateTaskBinding? = null
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
                viewModel.onTriggerEvent(CreateTaskEvents.OnUpdateUri(uri))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateTaskBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        binding.taskImage.setOnClickListener {
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
                        viewModel.onTriggerEvent(CreateTaskEvents.OnRemoveHeadFromQueue)
                    }
                })
            setTaskProperties(
                title = state.title,
                body = state.body,
                uri = state.uri,
            )
            if (state.onPublishSuccess) {
                findNavController().popBackStack(R.id.taskFragment, false)
            }
        })
    }

    private fun setTaskProperties(
        title: String,
        body: String,
        uri: Uri?
    ) {
        if (uri != null) {
            Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(uri)
                .into(binding.taskImage)
        } else {
            Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(R.drawable.default_white)
                .into(binding.taskImage)
        }

        binding.taskTitle.setText(title)
        binding.taskDescription.setText(body)
    }

    private fun cacheState() {
        val title = binding.taskTitle.text.toString()
        val body = binding.taskDescription.text.toString()
        viewModel.onTriggerEvent(CreateTaskEvents.OnUpdateTitle(title))
        viewModel.onTriggerEvent(CreateTaskEvents.OnUpdateBody(body))
    }

    override fun onPause() {
        super.onPause()
        cacheState()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.create_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.create -> {
                cacheState()
                viewModel.onTriggerEvent(CreateTaskEvents.PublishTask(activity))
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










