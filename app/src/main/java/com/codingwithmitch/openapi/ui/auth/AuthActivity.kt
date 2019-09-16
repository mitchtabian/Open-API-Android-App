package com.codingwithmitch.openapi.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.BaseActivity
import com.codingwithmitch.openapi.ui.auth.state.AuthStateEvent
import com.codingwithmitch.openapi.ui.main.MainActivity
import com.codingwithmitch.openapi.util.SuccessHandling.NetworkSuccessResponses.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import com.codingwithmitch.openapi.viewmodels.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject

class AuthActivity : BaseActivity() {

    private val TAG: String = "AppDebug"

    lateinit var viewModel: AuthViewModel

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        Log.d(TAG, "AuthActivity: onCreate: called.")

        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        subscribeObservers()
        checkPreviousAuthUser()
    }

    fun subscribeObservers(){

        viewModel.dataState.observe(this, Observer{ dataState ->
            Log.d(TAG, "AuthActivity, subscribeObservers: ${dataState}")
            onDataStateChange(dataState)
            dataState.data?.let{ data ->
                data.response?.let{event ->
                    event.peekContent().let{ response ->
                        response.message?.let{ message ->
                            if(message.equals(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE)){
                                onFinishCheckPreviousAuthUser()
                            }
                        }
                    }
                }
                data.data?.let { event ->
                    event.getContentIfNotHandled()?.let {
                        it.authToken?.let {
                            Log.d(TAG, "AuthActivity, DataState: ${it}")
                            viewModel.setAuthToken(it)
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(this, Observer{
            Log.d(TAG, "AuthActivity, subscribeObservers: AuthViewState: ${it}")
            it.authToken?.let{
                sessionManager.login(it)
            }
        })


        sessionManager.cachedToken.observe(this, Observer{ dataState ->
            Log.d(TAG, "AuthActivity, subscribeObservers: AuthDataState: ${dataState}")
            dataState.let{ authToken ->
                if(authToken != null && authToken.account_pk != -1 && authToken.token != null){
                    navMainActivity()
                }
            }
        })
    }

    private fun onFinishCheckPreviousAuthUser(){
        fragment_container.visibility = View.VISIBLE
    }

    private fun checkPreviousAuthUser(){
        viewModel.setStateEvent(AuthStateEvent.CheckPreviousAuthEvent())
    }

    fun navMainActivity(){
        Log.d(TAG, "navMainActivity: called.")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(bool: Boolean){
        if(bool){
            progress_bar.visibility = View.VISIBLE
        }
        else{
            progress_bar.visibility = View.GONE
        }
    }

    override fun setActionBarTitle(title: String) {
        //ignore
    }

    override fun expandAppBar() {
        // ignore
    }

    override fun isStoragePermissionGranted(): Boolean {
        // ignore
        return true
    }

}





















