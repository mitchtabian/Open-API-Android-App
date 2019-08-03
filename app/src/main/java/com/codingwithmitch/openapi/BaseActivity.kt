package com.codingwithmitch.openapi

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.session.SessionResource
import com.codingwithmitch.openapi.ui.auth.AuthActivity
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(){

    private val TAG: String = "AppDebug"

    @Inject
    lateinit var sessionManager: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        subscribeObervers()
    }

    fun subscribeObervers(){
        sessionManager.observeSession().observe(this, Observer {
            if(it.authToken == null){
                navAuthActivity()
            }
        })
    }

    fun logout(){
        sessionManager.logout()
    }

    fun navAuthActivity(){
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }


}














