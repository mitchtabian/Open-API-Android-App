package com.codingwithmitch.openapi.presentation.auth.register

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.openapi.business.domain.util.StateMessage
import com.codingwithmitch.openapi.business.domain.util.UIComponentType
import com.codingwithmitch.openapi.business.domain.util.doesMessageAlreadyExistInQueue
import com.codingwithmitch.openapi.business.interactors.auth.Register
import com.codingwithmitch.openapi.presentation.session.SessionEvents
import com.codingwithmitch.openapi.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
@Inject
constructor(
    private val register: Register,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val TAG: String = "AppDebug"

    val state: MutableLiveData<RegisterState> = MutableLiveData(RegisterState())

    fun onTriggerEvent(event: RegisterEvents) {
        when (event) {
            is RegisterEvents.Register -> {
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
            is RegisterEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun removeHeadFromQueue() {
        state.value?.let { state ->
            try {
                val queue = state.queue
                queue.remove() // can throw exception if empty
                this.state.value = state.copy(queue = queue)
            } catch (e: Exception) {
                Log.d(TAG, "removeHeadFromQueue: Nothing to remove from DialogQueue")
            }
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        state.value?.let { state ->
            val queue = state.queue
            if(!stateMessage.doesMessageAlreadyExistInQueue(queue = queue)){
                if(!(stateMessage.response.uiComponentType is UIComponentType.None)){
                    queue.add(stateMessage)
                    this.state.value = state.copy(queue = queue)
                }
            }
        }
    }

    private fun register(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ) {
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

}





































