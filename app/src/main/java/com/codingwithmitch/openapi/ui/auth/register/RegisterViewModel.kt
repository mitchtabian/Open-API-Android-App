package com.codingwithmitch.openapi.ui.auth.register

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.openapi.interactors.auth.Login
import com.codingwithmitch.openapi.interactors.auth.Register
import com.codingwithmitch.openapi.session.SessionEvents
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.util.PreferenceKeys
import com.codingwithmitch.openapi.util.StateMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
@Inject
constructor(
    private val editor: SharedPreferences.Editor,
    private val register: Register,
    private val sessionManager: SessionManager,
): ViewModel()
{
    val state: MutableLiveData<RegisterState> = MutableLiveData(RegisterState())

    fun onTriggerEvent(event: RegisterEvents){
        when(event){
            is RegisterEvents.Register ->{
                register(
                    email = event.email,
                    username = event.username,
                    password = event.password,
                    confirmPassword = event.confirmPassword,
                )
            }
            is RegisterEvents.OnUpdateEmail -> {
                onUpdateEmail(event.email)
            }
            is RegisterEvents.OnUpdateUsername -> {
                onUpdateUsername(event.username)
            }
            is RegisterEvents.OnUpdatePassword -> {
                onUpdatePassword(event.password)
            }
            is RegisterEvents.OnUpdateConfirmPassword -> {
                onUpdateConfirmPassword(event.confirmPassword)
            }
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        // TODO
    }

    private fun register(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ){
        // TODO("Perform some simple form validation?")
        state.value?.let { state ->
            register.execute(
                email = email,
                username = username,
                password = password,
                confirmPassword = confirmPassword,
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

    private fun onUpdateConfirmPassword(confirmPassword: String) {
        state.value?.let { state ->
            this.state.value = state.copy(confirmPassword = confirmPassword)
        }
    }

    private fun onUpdatePassword(password: String) {
        state.value?.let { state ->
            this.state.value = state.copy(password = password)
        }
    }

    private fun onUpdateUsername(username: String) {
        state.value?.let { state ->
            this.state.value = state.copy(username = username)
        }
    }

    private fun onUpdateEmail(email: String) {
        state.value?.let { state ->
            this.state.value = state.copy(email = email)
        }
    }

    private fun saveAuthUser(email: String) {
        editor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        editor.apply()
    }
}





































