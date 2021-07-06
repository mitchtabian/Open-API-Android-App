package com.codingwithmitch.openapi.presentation.main.account.password

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.business.domain.util.StateMessageCallback
import com.codingwithmitch.openapi.presentation.main.account.BaseAccountFragment
import com.codingwithmitch.openapi.presentation.util.processQueue
import kotlinx.android.synthetic.main.fragment_change_password.*

class AccountPasswordFragment : BaseAccountFragment(R.layout.fragment_change_password) {

    private val viewModel: AccountPasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        update_password_button.setOnClickListener {
            cacheState()
            viewModel.onTriggerEvent(AccountPasswordEvents.ChangePassword(
                currentPassword = input_current_password.text.toString(),
                newPassword = input_new_password.text.toString(),
                confirmNewPassword = input_confirm_password.text.toString()
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
        input_current_password.setText(currentPassword)
        input_new_password.setText(newPassword)
        input_confirm_password.setText(confirmNewPassword)
    }

    override fun onPause() {
        super.onPause()
        cacheState()
    }

    private fun cacheState(){
        viewModel.onTriggerEvent(AccountPasswordEvents.OnUpdateCurrentPassword(input_current_password.text.toString()))
        viewModel.onTriggerEvent(AccountPasswordEvents.OnUpdateNewPassword(input_new_password.text.toString()))
        viewModel.onTriggerEvent(AccountPasswordEvents.OnUpdateConfirmNewPassword(input_confirm_password.text.toString()))
    }
}




