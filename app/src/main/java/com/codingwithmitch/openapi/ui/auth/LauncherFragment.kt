package com.codingwithmitch.openapi.ui.auth


import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.di.auth.AuthScope
import kotlinx.android.synthetic.main.fragment_launcher.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@AuthScope
class LauncherFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
): BaseAuthFragment(R.layout.fragment_launcher, viewModelFactory) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        register.setOnClickListener {
            navRegistration()
        }

        login.setOnClickListener {
            navLogin()
        }

        forgot_password.setOnClickListener {
            navForgotPassword()
        }

        focusable_view.requestFocus() // reset focus
    }

    fun navLogin(){
        findNavController().navigate(R.id.action_launcherFragment_to_loginFragment)
    }

    fun navRegistration(){
        findNavController().navigate(R.id.action_launcherFragment_to_registerFragment)
    }

    fun navForgotPassword(){
        findNavController().navigate(R.id.action_launcherFragment_to_forgotPasswordFragment)
    }

}





















