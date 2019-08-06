package com.codingwithmitch.openapi.api.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.codingwithmitch.openapi.api.ApiEmptyResponse
import com.codingwithmitch.openapi.api.ApiErrorResponse
import com.codingwithmitch.openapi.api.ApiSuccessResponse
import com.codingwithmitch.openapi.api.GenericApiResponse
import com.codingwithmitch.openapi.api.auth.network_responses.LoginResponse
import com.codingwithmitch.openapi.api.auth.network_responses.RegistrationResponse
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.ui.auth.state.AuthDataState
import kotlinx.coroutines.*
import java.lang.Exception


abstract class AuthNetworkBoundResource<ResponseType>
{

    private val TAG: String = "AppDebug"
    private val ERROR_RESPONSE = "Error"

    private val result = MediatorLiveData<AuthDataState>()

    init {
        result.value = AuthDataState.Loading

        val apiResponse = createCall()
        result.addSource(apiResponse){ response ->
            result.removeSource(apiResponse)
            when(response){
                is ApiSuccessResponse ->{
                    Log.d(TAG, "AuthNetworkBoundResource: ${response.body}")
                    when(response.body){
                        is RegistrationResponse ->{
                            handleRegistrationResponse(response.body)
                        }
                        is LoginResponse ->{
                            handleLoginResponse(response.body)
                        }
                        else -> {
                            onReturnError("Unknown error. Try restarting the app.")
                        }
                    }

                }
                is ApiErrorResponse ->{
                    Log.e(TAG, ": ${response.errorMessage}")
                    onReturnError(response.errorMessage)
                }
                is ApiEmptyResponse ->{
                    Log.e(TAG, ": Request returned NOTHING (HTTP 204).")
                    onReturnError("HTTP 204. Returned NOTHING.")
                }
            }
        }
    }

    fun handleRegistrationResponse(response: RegistrationResponse){
        if(response.response.equals(ERROR_RESPONSE) ){
            onReturnError(response.errorMessage)
        }
        else {
            CoroutineScope(Dispatchers.IO)
                .launch {
                    try{
                        if(saveAccountPropertiesLocally(AccountProperties(response.pk, response.email, response.username)) > -1){
                            // AccountProperties insert success

                            if(saveTokenLocally(AuthToken(response.pk, response.token)) > -1){
                                // AccountProperties insert success
                                saveUserToPrefs(response.email)
                                setValue(
                                    authState = AuthDataState.Data(AuthToken(response.pk, response.token))
                                )
                            }
                            else{
                                // insert fail
                                onReturnError("Error saving authentication token.\nTry restarting the app.")
                            }
                        }
                        else{
                            // insert fail
                            onReturnError("Error saving account properties.\nTry restarting the app.")
                        }

                    }catch (e: Exception){
                        Log.e(TAG, "handleRegistrationResponse: ${e.message}")
                        onReturnError(e.message)
                    }
                }
        }
    }

    fun handleLoginResponse(response: LoginResponse){
        if(response.response.equals(ERROR_RESPONSE) ){
            onReturnError(response.errorMessage)
        }
        else {
            CoroutineScope(Dispatchers.IO)
                .launch {
                    try{
                        if(saveAccountPropertiesLocally(AccountProperties(response.pk, response.email, "")) > -1){
                            if(saveTokenLocally(AuthToken(response.pk, response.token)) > -1){
                                // token insert success
                                saveUserToPrefs(response.email)
                                setValue(
                                    authState = AuthDataState.Data(AuthToken(response.pk, response.token))
                                )
                            }
                            else{
                                // insert fail
                                onReturnError("Error saving authentication token.\nTry restarting the app.")
                            }
                        }
                        else{
                            // insert fail
                            onReturnError("Error saving account properties.\nTry restarting the app.")
                        }

                    }catch (e: Exception){
                        Log.e(TAG, "handleLoginResponse: ${e.message}")
                        onReturnError(e.message)
                    }
                }
        }
    }



    fun onReturnError(errorMessage: String?){
        var msg = errorMessage
        if(msg == null){
            msg = "Uknown error"
        }
        setValue(
            AuthDataState.Error(msg)
        )
    }

    fun setValue(authState: AuthDataState){
        CoroutineScope(Dispatchers.Main)
            .launch {
                Log.d(TAG, "setValue: setting the result value")
                result.value = authState
            }
    }


    fun asLiveData() = result as LiveData<AuthDataState>

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseType>>

    abstract fun saveUserToPrefs(email: String)

    abstract suspend fun saveTokenLocally(authToken: AuthToken): Long

    abstract suspend fun saveAccountPropertiesLocally(accountProperties: AccountProperties): Long


}
