package com.codingwithmitch.openapi.ui.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingwithmitch.openapi.api.auth.OpenApiAuthService
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.util.PreferenceKeys
import javax.inject.Inject

class AuthActivityViewModel
@Inject
constructor(
    val sharedPreferences: SharedPreferences,
    val openApiAuthService: OpenApiAuthService): ViewModel()
{
    private val TAG: String = "AppDebug"

//    enum class ViewState{
//        REGISTER,
//        LOGIN,
//        FORGOT_PASSWORD,
//        LAUNCHER,
//        AUTHENTICATED
//    }

    enum class ViewState{
        SHOW_PROGRESS,
        HIDE_PROGRESS,
    }

    private val viewState = MutableLiveData<ViewState>()
    private val authToken = MutableLiveData<AuthToken>()


    init{
        Log.d(TAG, "init: called.")
//        viewState.value = LAUNCHER
        checkPreviousAuthUser()
    }

    fun observeAuthState(): LiveData<AuthToken> {
        return authToken
    }

    fun observeViewState(): LiveData<ViewState> {
        return viewState
    }

    fun setViewState(v: ViewState){
        viewState.value = v
    }

    fun checkPreviousAuthUser(){
        Log.d(TAG, "checkPreviousAuthUser: ")
        val previousAuthUserEmail: String? = sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)

        if (previousAuthUserEmail != null) {
            retrieveAuthToken(previousAuthUserEmail)
        }
        else{
            // No previously authenticated user. Wait for user input
            Log.d(TAG, "No previously authenticated user. Waiting for user input...")
        }
    }


    fun retrieveAuthToken(email: String){

        val token: AuthToken? = AuthToken(1, "some_token")
        TODO("search auth_token table for token associated with email")


        if(token != null){
            // found token. User is authenticated.
            Log.d(TAG, "Token found... User is authenticated.")
            authToken.value = token // trigger navigation to MainActivity

        }
        else{
            // No token in local db. Wait for user input
            Log.d(TAG, "No token found in local db for user: $email. Waiting for user input...")
        }
    }

    fun checkIfAccountExists(email: String){

    }


}




























