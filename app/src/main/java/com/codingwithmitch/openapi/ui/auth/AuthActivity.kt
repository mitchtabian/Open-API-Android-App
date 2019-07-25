package com.codingwithmitch.openapi.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.ui.auth.state.ViewState.ViewStateValue.*
import com.codingwithmitch.openapi.ui.main.MainActivity
import com.codingwithmitch.openapi.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class AuthActivity : DaggerAppCompatActivity() {

    private val TAG: String = "AppDebug"

    lateinit var progressBar: ProgressBar

    lateinit var viewModel: AuthActivityViewModel

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        progressBar = findViewById(R.id.progress_bar)

        Log.d(TAG, "AuthActivity: started")

        viewModel = ViewModelProviders.of(this, providerFactory).get(AuthActivityViewModel::class.java)

        subscribeObservers()
    }

    fun subscribeObservers(){
        viewModel.observeViewState().observe(this, Observer {

            when(it.viewStateValue){

                CLEAR_ALL -> { // Might not need this?
                    clearViewState()
                }
                SHOW_PROGRESS -> {
                    displayProgressBar(true)
                }
                HIDE_PROGRESS -> {
                    displayProgressBar(false)
                }
                SHOW_ERROR_DIALOG -> {
                    it.message?.let { theMessage -> displayErrorDialog(theMessage) }
                }
                else -> {
                    clearViewState()
                }
            }
        })


        viewModel.observeAuthState().observe(this, Observer {
            it.authToken?.let {
                if(it.account_pk != -1 && it.token != null){
                    navMainActivity(it)
                }
            }
        })
    }

    fun navMainActivity(token: AuthToken){
        Log.d(TAG, "navMainActivity: called.")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(getString(R.string.auth_token), token)
        startActivity(intent)
    }

    fun displayProgressBar(bool: Boolean){
        if(bool){
            progressBar.visibility = View.VISIBLE
        }
        else{
            progressBar.visibility = View.GONE
        }
    }

    fun displayErrorDialog(errorMessage: String){
        MaterialDialog(this)
            .title(R.string.text_error)
            .message(text = errorMessage){
                lineSpacing(2F)
            }
            .positiveButton(R.string.text_ok)
            .show()
    }

    fun clearViewState(){
        displayProgressBar(false)
    }
}





















