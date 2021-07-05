package com.codingwithmitch.openapi.ui.auth.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.auth.BaseAuthFragment
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : BaseAuthFragment(R.layout.fragment_register) {

    private val viewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        register_button.setOnClickListener {
            register()
        }
        subscribeObservers()
        viewModel.state.value?.let { state ->
            setRegisterFields(
                email = state.email,
                username = state.username,
                password = state.password,
                confirmPassword = state.confirmPassword
            )
        }
        viewModel.state.value?.let { state ->
        }
    }

    private fun subscribeObservers() {
        viewModel.state.observe(viewLifecycleOwner, { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)
        })
    }

    private fun setRegisterFields(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ){
        input_email.setText(email)
        input_username.setText(username)
        input_password.setText(password)
        input_password_confirm.setText(confirmPassword)
    }

    private fun cacheState(){
        viewModel.onTriggerEvent(RegisterEvents.OnUpdateEmail(input_email.text.toString()))
        viewModel.onTriggerEvent(RegisterEvents.OnUpdateUsername(input_username.text.toString()))
        viewModel.onTriggerEvent(RegisterEvents.OnUpdatePassword(input_password.text.toString()))
        viewModel.onTriggerEvent(RegisterEvents.OnUpdateConfirmPassword(input_password_confirm.text.toString()))
    }

    private fun register() {
        cacheState()
        viewModel.onTriggerEvent(RegisterEvents.Register(
            email = input_email.text.toString(),
            username = input_username.text.toString(),
            password = input_password.text.toString(),
            confirmPassword = input_password_confirm.text.toString(),
        ))
    }

    override fun onPause() {
        super.onPause()
        cacheState()
    }
}