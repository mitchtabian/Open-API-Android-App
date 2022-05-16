package com.templateapp.cloudapi.presentation.main.account.users.detail

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.domain.util.Constants.Companion.BASE_URL
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_TASK_DOES_NOT_EXIST
import com.templateapp.cloudapi.databinding.FragmentViewAccountBinding
import com.templateapp.cloudapi.presentation.main.account.BaseAccountFragment
import com.templateapp.cloudapi.presentation.util.processQueue
import android.widget.Switch




class ViewAccountFragment : BaseAccountFragment()
{

    private val viewModel: ViewAccountViewModel by viewModels()

    private var _binding: FragmentViewAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
        uiCommunicationListener.expandAppBar()

        // If an update occurred from UpdateTaskFragment, refresh the Task
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(SHOULD_REFRESH)?.observe(viewLifecycleOwner) { shouldRefresh ->
            shouldRefresh?.run {
                viewModel.onTriggerEvent(ViewAccountEvents.Refresh)
                findNavController().currentBackStackEntry?.savedStateHandle?.set(SHOULD_REFRESH, null)
            }
        }
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            if(state.queue.peek()?.response?.message == ERROR_TASK_DOES_NOT_EXIST){
                 findNavController().popBackStack(R.id.accountFragment, false)
            }else{
                processQueue(
                    context = context,
                    queue = state.queue,
                    stateMessageCallback = object: StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.onTriggerEvent(ViewAccountEvents.OnRemoveHeadFromQueue)
                        }
                    })
            }

            state.account?.let { setAccountProperties(it) }

            if(state.isDeleteComplete){
                findNavController().popBackStack(R.id.accountFragment, false)
            }
        })
    }

    private fun setAccountProperties(account: Account){

        binding.accountName.setText(account.name)
        binding.age.setText(account.age.toString())
        binding.email.setText(account.email)
        binding.enabled.setChecked(account.enabled)
        binding.role.setText(account.role.title)

        var createdAtList: List<String> = account.createdAt.split('T')
        var updatedAtList: List<String> = account.updatedAt.split('T')


        var createdAtTime = createdAtList[1].split(':')
        var updatedAtTime = updatedAtList[1].split(':')

        var createdAt = createdAtList[0] + " " + createdAtTime[0] + ":" + createdAtTime[1]
        var updatedAt = updatedAtList[0] + " " + updatedAtTime[0] + ":" + updatedAtTime[1]

        binding.createdAt.setText(createdAt)
        binding.updatedAt.setText(updatedAt)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            inflater.inflate(R.menu.edit_view_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when(item.itemId){
                R.id.edit -> {
                    navUpdateTaskFragment()
                    return true
                }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navUpdateTaskFragment(){
        try{
            viewModel.state.value?.let { state ->
                state.account?.let { account ->
                    val bundle = bundleOf("accountId" to account._id)
                    findNavController().navigate(R.id.action_showAccountFragment_to_changeAccountFragment, bundle)
                } ?: throw Exception("Null Task")
            }?: throw Exception("Null Task")
        }catch (e: Exception){
            e.printStackTrace()
            viewModel.onTriggerEvent(
                ViewAccountEvents.Error(
                    stateMessage = StateMessage(
                        response = Response(
                            message = e.message,
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        )
                    )
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}







