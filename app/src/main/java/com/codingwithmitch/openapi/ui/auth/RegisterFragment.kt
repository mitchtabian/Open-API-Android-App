package com.codingwithmitch.openapi.ui.auth


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.util.TextWatcherCallback


class RegisterFragment : BaseAuthFragment() {


    lateinit var viewModel: AuthViewModel
    lateinit var inputEmail: EditText
    lateinit var inputUsername: EditText
    lateinit var inputPassword: EditText
    lateinit var inputConfirmPassword: EditText

    lateinit var textWatcher: RegisterFragmentTextWatcher

    val textWatchCallback = object: TextWatcherCallback{

        override fun afterTextChanged(fieldId: Int, text: String?) {
            when(fieldId){
                R.id.input_email -> {
                    viewModel.setViewState(registration_email = text)
                }
                R.id.input_username ->{
                    viewModel.setViewState(registration_username = text)
                }
                R.id.input_password ->{
                    viewModel.setViewState(registration_password = text)
                }
                R.id.input_password_confirm ->{
                    viewModel.setViewState(registration_confirm_password = text)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inputEmail = view.findViewById(R.id.input_email)
        inputUsername = view.findViewById(R.id.input_username)
        inputPassword = view.findViewById(R.id.input_password)
        inputConfirmPassword = view.findViewById(R.id.input_password_confirm)
        viewModel = activity?.run {
            ViewModelProviders.of(this, providerFactory).get(AuthViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        view.findViewById<Button>(R.id.register_button).setOnClickListener {
            register()
        }

        restoreFieldValues()
        initTextWatcher()
    }

    fun register(){
        viewModel.attemptRegistration()
    }

    fun restoreFieldValues(){
        viewModel.observeViewState().observe(viewLifecycleOwner, Observer {
            it.registrationFields?.run {
                this.registration_email?.let{inputEmail.setText(it)}
                this.registration_username?.let{inputUsername.setText(it)}
                this.registration_password?.let{inputPassword.setText(it)}
                this.registration_confirm_password?.let{inputConfirmPassword.setText(it)}
            }
            viewModel.observeViewState().removeObservers(viewLifecycleOwner)
        })

    }

    fun initTextWatcher(){
        textWatcher = RegisterFragmentTextWatcher(textWatchCallback)
        textWatcher.registerField(inputEmail)
        textWatcher.registerField(inputUsername)
        textWatcher.registerField(inputPassword)
        textWatcher.registerField(inputConfirmPassword)
    }

    class RegisterFragmentTextWatcher constructor(val callback: TextWatcherCallback){

        fun registerField(editText: EditText){
            editText.addTextChangedListener(object: TextWatcher{
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
















