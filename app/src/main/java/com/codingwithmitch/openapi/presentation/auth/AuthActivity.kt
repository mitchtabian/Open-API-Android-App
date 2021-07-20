package com.codingwithmitch.openapi.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.business.domain.util.StateMessageCallback
import com.codingwithmitch.openapi.databinding.ActivityAuthBinding
import com.codingwithmitch.openapi.presentation.BaseActivity
import com.codingwithmitch.openapi.presentation.main.MainActivity
import com.codingwithmitch.openapi.presentation.session.SessionEvents
import com.codingwithmitch.openapi.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity()
{

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subscribeObservers()
    }

    private fun subscribeObservers(){
        sessionManager.state.observe(this) { state ->
            displayProgressBar(state.isLoading)
            processQueue(
                context = this,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        sessionManager.onTriggerEvent(SessionEvents.OnRemoveHeadFromQueue)
                    }
                }
            )
            if (state.didCheckForPreviousAuthUser) {
                onFinishCheckPreviousAuthUser()
            }
            if (state.authToken != null && state.authToken.accountPk != -1) {
                navMainActivity()
            }
        }
    }

    private fun onFinishCheckPreviousAuthUser(){
        binding.fragmentContainer.visibility = View.VISIBLE
        binding.splashLogo.visibility = View.INVISIBLE
    }

    private fun navMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(isLoading: Boolean){
        if(isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }
        else{
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun expandAppBar() {
        // ignore
    }

}










