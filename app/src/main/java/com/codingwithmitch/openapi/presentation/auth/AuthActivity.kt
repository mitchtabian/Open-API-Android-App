package com.codingwithmitch.openapi.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.business.domain.util.StateMessageCallback
import com.codingwithmitch.openapi.presentation.BaseActivity
import com.codingwithmitch.openapi.presentation.main.MainActivity
import com.codingwithmitch.openapi.presentation.session.SessionEvents
import com.codingwithmitch.openapi.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_auth.*

@AndroidEntryPoint
class AuthActivity : BaseActivity()
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        subscribeObservers()
    }

    private fun subscribeObservers(){
        sessionManager.state.observe(this, { state ->
            displayProgressBar(state.isLoading)
            processQueue(
                context = this,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        sessionManager.onTriggerEvent(SessionEvents.OnRemoveHeadFromQueue)
                    }
                }
            )
            if(state.didCheckForPreviousAuthUser){
                onFinishCheckPreviousAuthUser()
            }
            if(state.authToken != null && state.authToken.accountPk != -1){
                navMainActivity()
            }
        })
    }

    private fun onFinishCheckPreviousAuthUser(){
        fragment_container.visibility = View.VISIBLE
        splash_logo.visibility = View.INVISIBLE
    }

    fun navMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(isLoading: Boolean){
        if(isLoading){
            progress_bar.visibility = View.VISIBLE
        }
        else{
            progress_bar.visibility = View.GONE
        }
    }

    override fun expandAppBar() {
        // ignore
    }

}










