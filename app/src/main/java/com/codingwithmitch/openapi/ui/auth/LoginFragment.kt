package com.codingwithmitch.openapi.ui.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import com.codingwithmitch.openapi.ui.auth.state.AuthStateEvent.*
import com.codingwithmitch.openapi.ui.auth.state.LoginFields
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject


class LoginFragment : DaggerFragment() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AuthViewModel



//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        viewModel = activity?.run {
//            ViewModelProviders.of(this, providerFactory).get(AuthViewModel::class.java)
//        }?: throw Exception("Invalid Activity")
//    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       viewModel= ViewModelProviders.of(this, providerFactory).get(AuthViewModel::class.java)
        login_button.setOnClickListener { login() }
        subscribeObservers()
    }

    fun login(){
        viewModel.setStateEvent(
            LoginAttemptEvent(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }

    fun subscribeObservers(){
        viewModel.viewState.observe(viewLifecycleOwner, Observer{
            it.loginFields?.let{
                it.login_email?.let{input_email.setText(it)}
                it.login_password?.let{input_password.setText(it)}
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setLoginFields(
            LoginFields(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }
}















