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
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : BaseAuthFragment() {

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
                this.registration_email?.let{input_email.setText(it)}
                this.registration_username?.let{input_username.setText(it)}
                this.registration_password?.let{input_password.setText(it)}
                this.registration_confirm_password?.let{input_password_confirm.setText(it)}
            }
            viewModel.observeViewState().removeObservers(viewLifecycleOwner)
        })

    }

    fun initTextWatcher(){
        textWatcher = RegisterFragmentTextWatcher(textWatchCallback)
        textWatcher.registerField(input_email)
        textWatcher.registerField(input_username)
        textWatcher.registerField(input_password)
        textWatcher.registerField(input_password_confirm)
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
















