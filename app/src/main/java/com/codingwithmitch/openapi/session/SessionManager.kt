package com.codingwithmitch.openapi.session

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.persistence.auth.AuthTokenDao
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {

    private val TAG: String = "AppDebug"

    private val _cachedToken: MutableLiveData<AuthToken?> = MutableLiveData()

    val cachedToken: LiveData<AuthToken?>
        get() = _cachedToken

    fun login(newValue: AuthToken){
        setValue(newValue)
    }

    fun logout(){
        Log.d(TAG, "logout: ")

        CoroutineScope(IO).launch{
            var errorMessage: String? = null
            try{
                _cachedToken.value!!.account_pk?.let { authTokenDao.nullifyToken(it)
                } ?: throw CancellationException("Token Error. Logging out user.")
            }catch (e: CancellationException) {
                Log.e(TAG, "CancellationException: ${e.message}")
                errorMessage = e.message
            }
            catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.message}")
                errorMessage = errorMessage + "\n" + e.message
            }
            finally {
                errorMessage?.let{
                    Log.e(TAG, "finally: logout: ${errorMessage}" )
                }
                Log.d(TAG, "logout: finally")
                setValue(null)
            }
        }
    }

    fun setValue(newValue: AuthToken?) {
        CoroutineScope(Main).launch {
            if (_cachedToken.value != newValue) {
                _cachedToken.value = newValue
            }
        }
    }


}
