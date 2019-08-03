package com.codingwithmitch.openapi.session

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codingwithmitch.openapi.persistence.AuthTokenDao
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(val authTokenDao: AuthTokenDao) {

    private val TAG: String = "AppDebug";

    private val cachedToken = MutableLiveData<SessionResource>()

    fun observeSession(): LiveData<SessionResource>{
        return cachedToken
    }

    fun logout(){
        Log.d(TAG, "logout: ")

        // show loading
        setValue(SessionResource(
            authToken = cachedToken.value?.authToken,
            errorMessage = null,
            loading = true)
        )

        CoroutineScope(IO).launch{
            var errorMessage: String? = null
            try{
                cachedToken.value!!.authToken!!.account_pk?.let {
                    authTokenDao.nullifyToken(it)
                } ?: throw CancellationException("Token Error. Logging out user.")
            }catch (e: CancellationException) {
                Log.e(TAG, "logout: ${e.message}")
                errorMessage = e.message
            }
            catch (e: Exception) {
                Log.e(TAG, "logout: ${e.message}")
                errorMessage = errorMessage + "\n" + e.message
            }
            finally {
                Log.d(TAG, "logout: finally")
                setValue(SessionResource(
                    authToken = null,
                    errorMessage = errorMessage,
                    loading = false)
                )
            }
        }
    }

    fun login(newValue: SessionResource){
        setValue(newValue)
    }

    fun setValue(newValue: SessionResource) {
        GlobalScope.launch(Main) {
            if (cachedToken.value != newValue) {
                cachedToken.value = newValue
            }
        }
    }
}
























