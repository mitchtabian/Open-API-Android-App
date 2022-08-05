package com.templateapp.cloudapi.presentation.main.task.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.domain.util.Constants.Companion.BASE_URL
import com.templateapp.cloudapi.business.domain.util.StateMessageCallback
import com.templateapp.cloudapi.databinding.FragmentUpdateTaskBinding
import com.templateapp.cloudapi.presentation.main.task.BaseTaskFragment
import com.templateapp.cloudapi.presentation.main.task.detail.SHOULD_REFRESH
import com.templateapp.cloudapi.presentation.util.processQueue

class UpdateTaskFragment : BaseTaskFragment() {

    private val requestOptions = RequestOptions
        .placeholderOf(R.drawable.default_image)
        .error(R.drawable.default_image)

    private val viewModel: UpdateTaskViewModel by viewModels()

    private var authToken: AuthToken? = null
    private var _binding: FragmentUpdateTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateTaskBinding.inflate(layoutInflater)
        return binding.root
    }

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
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
            viewModel.onTriggerEvent(UpdateTaskEvents.OnUpdateUri(uri))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()

        binding.imageContainer.setOnClickListener {
            if (uiCommunicationListener.isStoragePermissionGranted()) {
                cropActivityResultLauncher.launch(null)
            }
        }

        binding.updateTextview.setOnClickListener {
            if (uiCommunicationListener.isStoragePermissionGranted()) {
                cropActivityResultLauncher.launch(null)
            }
        }

    }

    fun subscribeObservers() {
        viewModel.state.observe(viewLifecycleOwner, { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)
            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(UpdateTaskEvents.OnRemoveHeadFromQueue)
                    }
                })

            authToken = viewModel.sessionManager.state.value?.authToken

            state.task?.let { task ->
                val image = state.newImageUri
                setTaskProperties(
                    task.title,
                    task.description,
                    image ?: task.image.toUri()
                )
            }
            if (state.isUpdateComplete) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    SHOULD_REFRESH,
                    true
                )
                findNavController().popBackStack(R.id.viewTaskFragment, false)
            }
        })
    }

    private fun setTaskProperties(title: String?, body: String?, image: Uri?) {



        val ABC = "application/json";
        if(authToken!=null) {
            image?.let {
                if ("content://" in image.toString()) {
                    val url = it.toString()
                    val glideUrl = GlideUrl(
                        url,
                        LazyHeaders.Builder()
                            .addHeader("Authorization", authToken.toString())
                            .addHeader("Accept", ABC)
                            .build()

        )
        Glide.with(this)
            .setDefaultRequestOptions(requestOptions)
            .load(glideUrl)
            .into(binding.taskImage)
    } else {

        val url ="http://192.168.1.10:3000/" + it
        val glideUrl = GlideUrl(
            url,
            LazyHeaders.Builder()
                .addHeader("Authorization", authToken.toString())
                .addHeader("Accept", ABC)
                .build()
        )
        Glide.with(this)
            .setDefaultRequestOptions(requestOptions)
            .load(glideUrl)
            .into(binding.taskImage)
    }
}
    binding.taskTitle.setText(title)
    binding.taskDescription.setText(body)
}
    }

    private fun saveChanges() {
        cacheState()
        viewModel.onTriggerEvent(UpdateTaskEvents.Update(activity))
        uiCommunicationListener.hideSoftKeyboard()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                saveChanges()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun cacheState() {
        val title = binding.taskTitle.text.toString()
        val body = binding.taskDescription.text.toString()
        viewModel.onTriggerEvent(UpdateTaskEvents.OnUpdateTitle(title))
        viewModel.onTriggerEvent(UpdateTaskEvents.OnUpdateBody(body))
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










