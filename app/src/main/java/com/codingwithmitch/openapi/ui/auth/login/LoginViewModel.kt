package com.codingwithmitch.openapi.ui.auth.login

import android.content.SharedPreferences
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
    val state: MutableLiveData<LoginState> = MutableLiveData()

    fun onTriggerEvent(event: LoginEvents){
        when(event){
            is LoginEvents.Login ->{
                login(email = event.email, password = event.password)
            }
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        // TODO
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





































