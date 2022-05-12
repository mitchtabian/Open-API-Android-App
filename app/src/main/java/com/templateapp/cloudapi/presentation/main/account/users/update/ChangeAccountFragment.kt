package com.templateapp.cloudapi.presentation.main.account.users.update

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.util.StateMessageCallback
import com.templateapp.cloudapi.databinding.FragmentChangeAccountBinding
import com.templateapp.cloudapi.databinding.FragmentUpdateAccountBinding
import com.templateapp.cloudapi.presentation.main.account.BaseAccountFragment
import com.templateapp.cloudapi.presentation.util.processQueue

class ChangeAccountFragment : BaseAccountFragment() {

    private val viewModel: ChangeAccountViewModel by viewModels()

    private var _binding: FragmentChangeAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeAccountBinding.inflate(layoutInflater)
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
                        viewModel.onTriggerEvent(ChangeAccountEvents.OnRemoveHeadFromQueue)
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
        binding.email.setText(account.email)
        binding.username.setText(account.name)
        binding.age.setText(account.age.toString())
        binding.enabled.setChecked(account.enabled)
    }

    private fun saveChanges(){
        viewModel.onTriggerEvent(ChangeAccountEvents.Update(
            email = binding.email.text.toString(),
            username = binding.username.text.toString(),
            age = Integer.parseInt(binding.age.text.toString()),
            enabled = Boolean.equals(binding.enabled.text),
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
        val email = binding.email.text.toString()
        val username = binding.username.text.toString()
        val age = binding.age.text.toString()
        val enabled = binding.enabled.text.toString()
        viewModel.onTriggerEvent(ChangeAccountEvents.OnUpdateEmail(email))
        viewModel.onTriggerEvent(ChangeAccountEvents.OnUpdateUsername(username))
        viewModel.onTriggerEvent(ChangeAccountEvents.OnUpdateAge(Integer.parseInt(age)))
        viewModel.onTriggerEvent(ChangeAccountEvents.OnUpdateEnabled(Boolean.equals(enabled)))
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






