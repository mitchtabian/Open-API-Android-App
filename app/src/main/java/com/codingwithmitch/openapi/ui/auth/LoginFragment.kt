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
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : BaseAuthFragment() {

    lateinit var textWatcher: LoginFragmentTextWatcher

    val textWatchCallback = object: TextWatcherCallback{

        override fun afterTextChanged(fieldId: Int, text: String?) {
            when(fieldId){
                R.id.input_email -> {
                    viewModel.setViewState(login_email = text)
                }
                R.id.input_password ->{
                    viewModel.setViewState(login_password = text)
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
        viewModel.observeViewState().observe(viewLifecycleOwner, Observer {
            it.loginFields?.run {
                this.login_email?.let{input_email.setText(it)}
                this.login_password?.let{input_password.setText(it)}
            }
            viewModel.observeViewState().removeObservers(viewLifecycleOwner)
        })

    }

    fun initTextWatcher(){
        textWatcher = LoginFragmentTextWatcher(textWatchCallback)
        textWatcher.registerField(input_email)
        textWatcher.registerField(input_password)
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
