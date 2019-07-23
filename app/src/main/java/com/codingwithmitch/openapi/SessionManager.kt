package com.codingwithmitch.openapi

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codingwithmitch.openapi.api.auth.OpenApiAuthService
import com.codingwithmitch.openapi.di.main.MainScope
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.ui.auth.AuthResource
import javax.inject.Inject
import javax.inject.Singleton

@MainScope
class SessionManager @Inject constructor() {

    private val TAG: String = "AppDebug";

//    private val cachedToken: MediatorLiveData<AuthResource<AuthToken>> = MediatorLiveData()
    private val cachedToken = MutableLiveData<AuthResource<AuthToken>>()

    fun observeAuthState(): LiveData<AuthResource<AuthToken>>{
        return cachedToken
    }


    fun logout(){
        Log.d(TAG, "logout: ")
        setValue(AuthResource.logout())
    }

    @MainThread
    private fun setValue(newValue: AuthResource<AuthToken>) {
        if (cachedToken.value != newValue) {
            cachedToken.value = newValue
        }
    }
}
























