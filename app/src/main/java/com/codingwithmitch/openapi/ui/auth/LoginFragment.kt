package com.codingwithmitch.openapi.ui.auth


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.api.auth.OpenApiAuthService
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.util.TextWatcherCallback
import com.codingwithmitch.openapi.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class LoginFragment : DaggerFragment() {

    private val TAG: String = "AppDebug"

    lateinit var viewModel: AuthActivityViewModel
    lateinit var inputEmail: EditText
    lateinit var inputPassword: EditText

    lateinit var textWatcher: LoginFragmentTextWatcher

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    val textWatchCallback = object: TextWatcherCallback{

        override fun afterTextChanged(fieldId: Int, text: String?) {
            when(fieldId){
                R.id.input_email -> {
                    viewModel.setAuthState(login_email = text)
                }
                R.id.input_password ->{
                    viewModel.setAuthState(login_password = text)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inputEmail = view.findViewById(R.id.input_email)
        inputPassword = view.findViewById(R.id.input_password)
        viewModel = activity?.run {
            ViewModelProviders.of(this, providerFactory).get(AuthActivityViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        view.findViewById<Button>(R.id.login_button).setOnClickListener {
            login()
        }

        restoreFieldValues()
        initTextWatcher()
    }

    fun login(){
        viewModel.attemptLogin()
    }

    fun restoreFieldValues(){
        viewModel.observeAuthState().observe(viewLifecycleOwner, Observer {
            it.registerState?.run {
                this.email?.let{inputEmail.setText(it)}
                this.password?.let{inputPassword.setText(it)}
            }
            viewModel.observeAuthState().removeObservers(viewLifecycleOwner)
        })

    }

    fun initTextWatcher(){
        textWatcher = LoginFragmentTextWatcher(textWatchCallback)
        textWatcher.registerField(inputEmail)
        textWatcher.registerField(inputPassword)
    }

    class LoginFragmentTextWatcher constructor(val callback: TextWatcherCallback){

        fun registerField(editText: EditText){
            editText.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    val text = s.toString()
                    callback.afterTextChanged(editText.id, text)
                }
            })
        }
    }

}
