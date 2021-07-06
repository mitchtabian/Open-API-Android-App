package com.codingwithmitch.openapi.presentation.auth.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.business.domain.util.StateMessageCallback
import com.codingwithmitch.openapi.presentation.auth.BaseAuthFragment
import com.codingwithmitch.openapi.presentation.util.processQueue
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseAuthFragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        login_button.setOnClickListener {
            cacheState()
            login()
        }
        viewModel.state.value?.let { state ->
            setLoginFields(email = state.email, password = state.password)
        }
    }

    fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)
            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(LoginEvents.OnRemoveHeadFromQueue)
                    }
            })
        })
    }

    private fun setLoginFields(email: String, password: String){
        input_email.setText(email)
        input_password.setText(password)
    }

    private fun login(){
        viewModel.onTriggerEvent(LoginEvents.Login(
            email = input_email.text.toString(),
            password = input_password.text.toString()
        ))
    }

    private fun cacheState(){
        viewModel.onTriggerEvent(LoginEvents.OnUpdateEmail(input_email.text.toString()))
        viewModel.onTriggerEvent(LoginEvents.OnUpdatePassword(input_password.text.toString()))
    }

    override fun onPause() {
        super.onPause()
        cacheState()
    }
}








