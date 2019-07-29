package com.codingwithmitch.openapi

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codingwithmitch.openapi.di.main.MainScope
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.repository.main.MainRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

@MainScope
class SessionManager @Inject constructor(val repository: MainRepository) {

    private val TAG: String = "AppDebug";

    private val cachedToken = MutableLiveData<AuthToken>()

    fun observeAuthState(): LiveData<AuthToken>{
        return cachedToken
    }


    fun logout(){
        Log.d(TAG, "logout: ")
        CoroutineScope(IO).launch{
            val logoutResult: Deferred<Int> = async {
                repository.logout(cachedToken.value!!.account_pk!!)
            }
            logoutResult.await()
            setValue(AuthToken(-1, null))
        }

    }

    fun setValue(newValue: AuthToken) {
        GlobalScope.launch(Main) {
            if (cachedToken.value != newValue) {
                cachedToken.value = newValue
            }
        }
    }
}
























