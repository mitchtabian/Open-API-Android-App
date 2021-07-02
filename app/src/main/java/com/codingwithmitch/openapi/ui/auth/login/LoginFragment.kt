package com.codingwithmitch.openapi.ui.auth.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.auth.BaseAuthFragment
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
    }

    fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            setLoginFields(email = state.email, password = state.password)
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








