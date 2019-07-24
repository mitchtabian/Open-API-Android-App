package com.codingwithmitch.openapi.ui.main

import android.os.Bundle
import android.util.Log
import com.codingwithmitch.openapi.BaseActivity
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.AuthToken

class MainActivity : BaseActivity() {

    private val TAG: String = "AppDebug"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getAuthTokenFromBundle()
    }

    fun getAuthTokenFromBundle(){
        if(intent.hasExtra(getString(R.string.auth_token))){
            Log.d(TAG, "TOKEN: ${intent.getParcelableExtra<AuthToken>(getString(R.string.auth_token))}")
        }
    }
}