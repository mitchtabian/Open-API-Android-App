package com.templateapp.cloudapi.presentation.main.task.detail

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.domain.util.Constants.Companion.BASE_URL
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_TASK_DOES_NOT_EXIST
import com.templateapp.cloudapi.databinding.FragmentViewTaskBinding
import com.templateapp.cloudapi.presentation.main.task.BaseTaskFragment
import com.templateapp.cloudapi.presentation.util.processQueue

class ViewTaskFragment : BaseTaskFragment()
{
    private val requestOptions = RequestOptions
        .placeholderOf(R.drawable.default_image)
        .error(R.drawable.default_image)

    private val viewModel: ViewTaskViewModel by viewModels()

    private var _binding: FragmentViewTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewTaskBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
        uiCommunicationListener.expandAppBar()

        binding.deleteButton.setOnClickListener {
            viewModel.onTriggerEvent(ViewTaskEvents.DeleteTask)
        }

        // If an update occurred from UpdateTaskFragment, refresh the Task
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(SHOULD_REFRESH)?.observe(viewLifecycleOwner) { shouldRefresh ->
            shouldRefresh?.run {
                viewModel.onTriggerEvent(ViewTaskEvents.Refresh)
                findNavController().currentBackStackEntry?.savedStateHandle?.set(SHOULD_REFRESH, null)
            }
        }
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            if(state.queue.peek()?.response?.message == ERROR_TASK_DOES_NOT_EXIST){
                 findNavController().popBackStack(R.id.taskFragment, false)
            }else{
                processQueue(
                    context = context,
                    queue = state.queue,
                    stateMessageCallback = object: StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.onTriggerEvent(ViewTaskEvents.OnRemoveHeadFromQueue)
                        }
                    })
            }

            state.task?.let { setTaskProperties(it) }

            if(state.isAuthor == true){
                adaptViewToAuthorMode()
            }

            if(state.isDeleteComplete){
                findNavController().popBackStack(R.id.taskFragment, false)
            }
        })
    }

    private fun adaptViewToAuthorMode(){
        activity?.invalidateOptionsMenu()
        binding.deleteButton.visibility = View.VISIBLE
    }

    private fun setTaskProperties(task: Task){
        Glide.with(this)
            .setDefaultRequestOptions(requestOptions)
            .load("http://192.168.1.10:3000/" + task.image)
            .into(binding.taskImage)
        binding.taskTitle.setText(task.title)
        binding.taskOwner.setText(task.username)
        binding.taskUpdateDate.setText(DateUtils.convertLongToStringDate(task.updatedAt))
        binding.taskDescription.setText(task.description)
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
                    navUpdateTaskFragment()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navUpdateTaskFragment(){
        try{
            viewModel.state.value?.let { state ->
                state.task?.let { task ->
                    val bundle = bundleOf("taskId" to task.id)
                    findNavController().navigate(R.id.action_viewTaskFragment_to_updateTaskFragment, bundle)
                } ?: throw Exception("Null Task")
            }?: throw Exception("Null Task")
        }catch (e: Exception){
            e.printStackTrace()
            viewModel.onTriggerEvent(ViewTaskEvents.Error(
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







