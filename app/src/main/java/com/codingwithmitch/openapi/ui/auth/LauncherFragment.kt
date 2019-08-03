package com.codingwithmitch.openapi.ui.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation

import com.codingwithmitch.openapi.R
import kotlinx.android.synthetic.main.fragment_launcher.*


class LauncherFragment : BaseAuthFragment() {


    lateinit var navController: NavController
    lateinit var viewModel: AuthViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_launcher, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        viewModel = activity?.run {
            ViewModelProviders.of(this, providerFactory).get(AuthViewModel::class.java)
        }?: throw Exception("Invalid Activity")


        register.setOnClickListener({
            navRegistration()
        })

        login.setOnClickListener({
            navLogin()
        })

        forgot_password.setOnClickListener({
            navForgotPassword()
        })
    }

    fun navLogin(){
        navController.navigate(R.id.action_launcherFragment_to_loginFragment)
    }

    fun navRegistration(){
        navController.navigate(R.id.action_launcherFragment_to_registerFragment)
    }

    fun navForgotPassword(){
        navController.navigate(R.id.action_launcherFragment_to_forgotPasswordFragment)
    }


}
