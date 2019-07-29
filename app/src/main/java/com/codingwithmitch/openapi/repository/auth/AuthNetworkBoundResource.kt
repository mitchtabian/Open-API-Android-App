package com.codingwithmitch.openapi.repository.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codingwithmitch.openapi.api.auth.network_responses.*
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.ui.auth.state.AuthState
import kotlinx.coroutines.*
import retrofit2.HttpException
import retrofit2.Response
import java.lang.Exception


abstract class AuthNetworkBoundResource<ResponseType>
{

    private val TAG: String = "AppDebug"
    private val ERROR_RESPONSE = "Error"

    private val result = MutableLiveData<AuthState>()

    init {
        CoroutineScope(Dispatchers.IO)
            .launch{
                withContext(Dispatchers.IO){
                    try{
                        val response = ApiResponse.create(createResponse())
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

                    }catch (e: Throwable) {
                        Log.e(TAG, "attempRegistration: ${e.message}")
                        onReturnError(e.message)

                    } catch (e: HttpException) {
                        Log.e(TAG, "attempRegistration: ${e.message}")
                        onReturnError(e.message())
                    }
                }
            }
    }

    fun handleRegistrationResponse(response: RegistrationResponse){
        if(response.response.equals(ERROR_RESPONSE) ){
            onReturnError(response.errorMessage)
        }
        else {
            saveUserToPrefs(response.email)
            CoroutineScope(Dispatchers.IO)
                .launch {
                    withContext(Dispatchers.IO) {
                        try{
                            if(saveAccountPropertiesLocally(AccountProperties(response.pk, response.email, response.username)) > -1){
                                // AccountProperties insert success

                                if(saveTokenLocally(AuthToken(response.pk, response.token)) > -1){
                                    // AccountProperties insert success
                                    onReturnSuccess(AuthToken(response.pk, response.token))
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
    }

    fun handleLoginResponse(response: LoginResponse){
        if(response.response.equals(ERROR_RESPONSE) ){
            onReturnError(response.errorMessage)
        }
        else {
            saveUserToPrefs(response.email)
            CoroutineScope(Dispatchers.IO)
                .launch {
                    withContext(Dispatchers.IO) {
                        try{
                            if(saveAccountPropertiesLocally(AccountProperties(response.pk, response.email, "")) > -1){
                                // AccountProperties insert success

                                if(saveTokenLocally(AuthToken(response.pk, response.token)) > -1){
                                    // AccountProperties insert success
                                    onReturnSuccess(AuthToken(response.pk, response.token))
                                }
                                else{
                                    // insert fail
                                    onReturnError("Error saving authentication token.\nTry restarting the app.")
                                }
                            }

                        }catch (e: Exception){
                            Log.e(TAG, "handleLoginResponse: ${e.message}")
                            onReturnError(e.message)
                        }

                    }
                }
        }
    }

    fun onReturnSuccess(authToken: AuthToken){
        setValue(
            AuthState(
                authToken = authToken

            )
        )
    }

    fun onReturnError(errorMessage: String?){
        setValue(
            AuthState(
                stateError = AuthState.StateError.onError(errorMessage)
            )
        )
    }

    fun setValue(authState: AuthState){
        CoroutineScope(Dispatchers.Main)
            .launch {
                Log.d(TAG, "setValue: setting the result value")
                result.value = authState
            }
    }


    fun asLiveData() = result as LiveData<AuthState>

    abstract suspend fun createResponse(): Response<ResponseType>

    abstract fun saveUserToPrefs(email: String)

    abstract suspend fun saveTokenLocally(authToken: AuthToken): Long

    abstract suspend fun saveAccountPropertiesLocally(accountProperties: AccountProperties): Long


}












