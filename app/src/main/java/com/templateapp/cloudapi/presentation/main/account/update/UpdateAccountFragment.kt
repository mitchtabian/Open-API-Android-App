package com.templateapp.cloudapi.presentation.main.account.update

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.util.StateMessageCallback
import com.templateapp.cloudapi.databinding.FragmentUpdateAccountBinding
import com.templateapp.cloudapi.presentation.main.account.BaseAccountFragment
import com.templateapp.cloudapi.presentation.util.processQueue

class UpdateAccountFragment : BaseAccountFragment() {

    private val viewModel: UpdateAccountViewModel by viewModels()

    private var _binding: FragmentUpdateAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)
            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(UpdateAccountEvents.OnRemoveHeadFromQueue)
                    }
                }
            )
            if(state.isUpdateComplete){
                findNavController().popBackStack(R.id.accountFragment, false)
            }
            state.account?.let { account ->
                setAccountDataFields(state.account)
            }
        })

    }

    private fun setAccountDataFields(account: Account){
        binding.inputEmail.setText(account.email)
        binding.inputUsername.setText(account.username)
    }

    private fun saveChanges(){
        viewModel.onTriggerEvent(UpdateAccountEvents.Update(
            email = binding.inputEmail.text.toString(),
            username = binding.inputUsername.text.toString()
        ))
        uiCommunicationListener.hideSoftKeyboard()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save -> {
                cacheState()
                saveChanges()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun cacheState(){
        val email = binding.inputEmail.text.toString()
        val username = binding.inputUsername.text.toString()
        viewModel.onTriggerEvent(UpdateAccountEvents.OnUpdateEmail(email))
        viewModel.onTriggerEvent(UpdateAccountEvents.OnUpdateUsername(username))
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






