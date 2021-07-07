package com.codingwithmitch.openapi.presentation.main.account.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.business.domain.util.StateMessageCallback
import com.codingwithmitch.openapi.databinding.FragmentChangePasswordBinding
import com.codingwithmitch.openapi.presentation.main.account.BaseAccountFragment
import com.codingwithmitch.openapi.presentation.util.processQueue

class AccountPasswordFragment : BaseAccountFragment() {

    private val viewModel: AccountPasswordViewModel by viewModels()

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.updatePasswordButton.setOnClickListener {
            cacheState()
            viewModel.onTriggerEvent(AccountPasswordEvents.ChangePassword(
                currentPassword = binding.inputCurrentPassword.text.toString(),
                newPassword = binding.inputNewPassword.text.toString(),
                confirmNewPassword = binding.inputConfirmPassword.text.toString()
            ))
        }

        subscribeObservers()
        viewModel.state.value?.let { state ->
            setPasswordFields(
                currentPassword = state.currentPassword,
                newPassword = state.newPassword,
                confirmNewPassword = state.confirmNewPassword
            )
        }
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)
            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(AccountPasswordEvents.OnRemoveHeadFromQueue)
                    }
                })
            if(state.isPasswordChangeComplete){
                findNavController().popBackStack(R.id.accountFragment, false)
            }
        })
    }

    private fun setPasswordFields(currentPassword: String, newPassword: String, confirmNewPassword: String){
        binding.inputCurrentPassword.setText(currentPassword)
        binding.inputNewPassword.setText(newPassword)
        binding.inputConfirmPassword.setText(confirmNewPassword)
    }

    override fun onPause() {
        super.onPause()
        cacheState()
    }

    private fun cacheState(){
        viewModel.onTriggerEvent(AccountPasswordEvents.OnUpdateCurrentPassword(binding.inputCurrentPassword.text.toString()))
        viewModel.onTriggerEvent(AccountPasswordEvents.OnUpdateNewPassword(binding.inputNewPassword.text.toString()))
        viewModel.onTriggerEvent(AccountPasswordEvents.OnUpdateConfirmNewPassword(binding.inputConfirmPassword.text.toString()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




