package com.codingwithmitch.openapi.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.BaseActivity
import com.codingwithmitch.openapi.ui.main.MainActivity
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










