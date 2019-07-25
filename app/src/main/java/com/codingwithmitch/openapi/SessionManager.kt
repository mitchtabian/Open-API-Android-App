package com.codingwithmitch.openapi

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codingwithmitch.openapi.di.main.MainScope
import com.codingwithmitch.openapi.models.AuthToken
import javax.inject.Inject

@MainScope
class SessionManager @Inject constructor() {

    private val TAG: String = "AppDebug";

//    private val cachedToken: MediatorLiveData<AuthResource<AuthToken>> = MediatorLiveData()
//    private val cachedToken = MutableLiveData<AuthNetworkBoundResource<AuthToken>>()
//
//    fun observeAuthState(): LiveData<AuthNetworkBoundResource<AuthToken>>{
//        return cachedToken
//    }


//    fun logout(){
//        Log.d(TAG, "logout: ")
//        setValue(AuthNetworkBoundResource.logout())
//    }

//    @MainThread
//    private fun setValue(newValue: AuthNetworkBoundResource<AuthToken>) {
//        if (cachedToken.value != newValue) {
//            cachedToken.value = newValue
//        }
//    }
}
























