package com.codingwithmitch.openapi.ui.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.persistence.AccountPropertiesDao
import com.codingwithmitch.openapi.session.SessionResource
import com.codingwithmitch.openapi.ui.BaseActivity
import com.codingwithmitch.openapi.ui.auth.state.AuthStateEvent
import com.codingwithmitch.openapi.ui.main.MainActivity
import com.codingwithmitch.openapi.util.PreferenceKeys
import com.codingwithmitch.openapi.util.SuccessHandling
import com.codingwithmitch.openapi.util.SuccessHandling.NetworkSuccessResponses.*
import com.codingwithmitch.openapi.util.SuccessHandling.NetworkSuccessResponses.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import com.codingwithmitch.openapi.viewmodels.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthActivity : BaseActivity() {

    private val TAG: String = "AppDebug"

    lateinit var viewModel: AuthViewModel

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewModel = ViewModelProviders.of(this, providerFactory).get(AuthViewModel::class.java)
        subscribeObservers()
        checkPreviousAuthUser()
    }

    fun subscribeObservers(){

        viewModel.viewState.observe(this, Observer{
            it.authToken?.let{
                sessionManager.login(SessionResource(it, null))
            }
        })

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
                            Log.d(TAG, "BaseAuthFragment, DataState: ${it}")
                            viewModel.setAuthToken(it)
                        }
                    }
                }
            }
        })

        sessionManager.observeSession().observe(this, Observer {
            it?.let {
                if(it.authToken?.account_pk != -1 && it.authToken?.token != null){
                    navMainActivity()
                }
                it.errorMessage?.let {
                    displayErrorDialog(it)
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


}





















