package com.codingwithmitch.openapi.ui.main.account

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.api.GenericResponse
import com.codingwithmitch.openapi.models.AccountProperties

import com.codingwithmitch.openapi.ui.main.BaseFragment
import com.codingwithmitch.openapi.ui.main.account.state.AccountDataState
import com.codingwithmitch.openapi.ui.main.account.state.AccountViewState
import kotlinx.android.synthetic.main.fragment_account.*


class AccountFragment : BaseFragment() {

    lateinit var viewModel: AccountViewModel

    private lateinit var accountStateChangeListener: AccountStateChangeListener


    private lateinit var emailField: TextView
    private lateinit var usernameField: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // setup back navigation for this graph
        setupActionBarWithNavController(R.id.accountFragment, activity as AppCompatActivity)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailField = view.findViewById<TextView>(R.id.input_email)
        usernameField = view.findViewById<TextView>(R.id.input_username)

        viewModel = ViewModelProviders.of(this, providerFactory).get(AccountViewModel::class.java)
        Log.d(TAG, "viewmodel : ${viewModel}")

        change_password.setOnClickListener{
            findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
        }

        logout_button.setOnClickListener {
            viewModel.logout()
        }

        save_changes_button.setOnClickListener {
            input_email.text?.toString()?.let { email ->
                input_username.text?.toString()?.let {username ->
                    viewModel.saveAccountProperties(email, username)
                    accountStateChangeListener.hideSoftKeyboard()
                }
            }

        }

        subscribeObservers()
    }

    private fun subscribeObservers(){
        viewModel.observeDataState().observe(this, Observer {
            // send state to activity for UI updates
            // ex: progress bar and material dialog
            accountStateChangeListener.onAccountDataStateChange(it)
            when(it){
                is AccountDataState.Error ->{
                    // handled by MainActivity through "accountStateChangeListener"
                }

                is AccountDataState.Loading ->{
                    // handled by MainActivity through "accountStateChangeListener"
                    it.accountProperties?.let { properties -> setAccountDataFields(properties) }
                }

                is AccountDataState.Data ->{
                    it.accountProperties?.let { properties -> setAccountDataFields(properties) }
                }
            }
        })

        viewModel.observeViewState().observe(this, Observer {
            if(it != null){
                accountStateChangeListener.onAccountViewStateChange(it)
            }
        })
    }

    private fun setAccountDataFields(accountProperties: AccountProperties){
        emailField.text = accountProperties.email
        usernameField.text = accountProperties.username
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            accountStateChangeListener = context as AccountStateChangeListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement OnArticleSelectedListene" )
        }
    }

}

























