package com.codingwithmitch.openapi.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.session.SessionResource
import com.codingwithmitch.openapi.ui.auth.state.AuthScreenState
import com.codingwithmitch.openapi.ui.main.MainActivity
import com.codingwithmitch.openapi.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class AuthActivity : DaggerAppCompatActivity() {

    private val TAG: String = "AppDebug"

    lateinit var progressBar: ProgressBar
    lateinit var fragmentContainer: FrameLayout

    lateinit var viewModel: AuthViewModel

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        progressBar = findViewById(R.id.progress_bar)
        fragmentContainer = findViewById(R.id.fragment_container)

        viewModel = ViewModelProviders.of(this, providerFactory).get(AuthViewModel::class.java)
        subscribeObservers()
        viewModel.checkPreviousAuthUser()

    }


    private fun onFinishCheckPreviousAuthUser(){
        fragmentContainer.visibility = View.VISIBLE
    }

    fun subscribeObservers(){
        viewModel.observeAuthScreenState().observe(this, Observer { authScreenState ->
            when(authScreenState){
                is AuthScreenState.Loading -> {
                    displayProgressBar(true)
                }
                is AuthScreenState.Data -> {
                    authScreenState.authToken?.let{
                        sessionManager.login(SessionResource(it, null))
                    }
                    if (authScreenState.authToken?.token == null){
                        onFinishCheckPreviousAuthUser()
                        displayProgressBar(false)
                    }

                }
                is AuthScreenState.Error -> {
                    displayProgressBar(false)
                    displayErrorDialog(authScreenState.errorMessage)
                }
            }
        })


        sessionManager.observeSession().observe(this, Observer {
            it?.let {
                if(it.authToken?.account_pk != -1 && it.authToken?.token != null){
                    navMainActivity()
                }
                when(it.loading){
                    true -> displayProgressBar(true)
                    false -> displayProgressBar(false)
                    else -> displayProgressBar(false)
                }
                it.errorMessage?.let {
                    displayErrorDialog(it)
                }
            }
        })
    }

    fun navMainActivity(){
        Log.d(TAG, "navMainActivity: called.")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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

}





















