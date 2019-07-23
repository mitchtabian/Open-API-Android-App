package com.codingwithmitch.openapi.ui.auth


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.ui.auth.AuthActivityViewModel.*
import com.codingwithmitch.openapi.ui.main.MainActivity
import com.codingwithmitch.openapi.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_launcher.*
import javax.inject.Inject


class LauncherFragment : DaggerFragment() {

    private val TAG: String = "AppDebug"

    lateinit var navController: NavController
    lateinit var viewModel: AuthActivityViewModel

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

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
            ViewModelProviders.of(this, providerFactory).get(AuthActivityViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        viewModel.observeAuthState().observe(viewLifecycleOwner, Observer {

            if(it != null){
                if (!it.token.equals("")){
                    navMainActivity(it)
                }
            }
        })

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

    fun navMainActivity(token: AuthToken){
        val intent = Intent(activity, MainActivity::class.java)
        intent.putExtra(getString(R.string.auth_token), token)
        startActivity(intent)
    }


}
