package com.codingwithmitch.openapi.ui.main

import android.os.Bundle
import android.util.Log
import com.codingwithmitch.openapi.BaseActivity
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.repository.auth.AuthRepository
import com.codingwithmitch.openapi.repository.main.MainRepository
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : BaseActivity() {

    private val TAG: String = "AppDebug"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getAuthTokenFromBundle()

        button_logout.setOnClickListener {
            CoroutineScope(IO).launch{
                logout()
            }
        }
    }

    fun getAuthTokenFromBundle(){
        if(intent.hasExtra(getString(R.string.auth_token))){
            Log.d(TAG, "TOKEN: ${intent.getParcelableExtra<AuthToken>(getString(R.string.auth_token))}")
            setAuthToken(intent.getParcelableExtra<AuthToken>(getString(R.string.auth_token)))
        }
    }
}