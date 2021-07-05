package com.codingwithmitch.openapi.ui.auth.login

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.openapi.interactors.auth.Login
import com.codingwithmitch.openapi.session.SessionEvents
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.util.PreferenceKeys
import com.codingwithmitch.openapi.util.StateMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject
constructor(
    private val editor: SharedPreferences.Editor,
    private val login: Login,
    private val sessionManager: SessionManager,
): ViewModel()
{

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<LoginState> = MutableLiveData(LoginState())

    fun onTriggerEvent(event: LoginEvents){
        Log.d(TAG, "onTriggerEvent: ${event}")
        when(event){
            is LoginEvents.Login ->{
                login(email = event.email, password = event.password)
            }
            is LoginEvents.OnUpdateEmail ->{
                onUpdateEmail(event.email)
            }
            is LoginEvents.OnUpdatePassword ->{
                onUpdatePassword(event.password)
            }
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        Log.d(TAG, "appendToMessageQueue: ${stateMessage.response.message}")
        // TODO
    }

    private fun onUpdateEmail(email: String){
        state.value?.let { state ->
            this.state.value = state.copy(email = email)
        }
    }

    private fun onUpdatePassword(password: String){
        state.value?.let { state ->
            this.state.value = state.copy(password = password)
        }
    }

    private fun login(email: String, password: String){
        // TODO("Perform some simple form validation")
        state.value?.let { state ->
            login.execute(
                email = email,
                password = password,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { authToken ->
                    saveAuthUser(email)
                    sessionManager.onTriggerEvent(SessionEvents.Login(authToken))
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun saveAuthUser(email: String) {
        editor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        editor.apply()
    }
}





































