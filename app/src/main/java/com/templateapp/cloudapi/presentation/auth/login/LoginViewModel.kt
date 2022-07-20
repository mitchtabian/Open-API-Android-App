package com.templateapp.cloudapi.presentation.auth.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.domain.util.StateMessage
import com.templateapp.cloudapi.business.domain.util.UIComponentType
import com.templateapp.cloudapi.business.domain.util.doesMessageAlreadyExistInQueue
import com.templateapp.cloudapi.business.interactors.auth.Login
import com.templateapp.cloudapi.presentation.session.SessionEvents
import com.templateapp.cloudapi.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject
constructor(
    private val login: Login,
    private val sessionManager: SessionManager,
): ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<LoginState> = MutableLiveData(LoginState())

    fun onTriggerEvent(event: LoginEvents){
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
            is LoginEvents.OnRemoveHeadFromQueue ->{
                removeHeadFromQueue()
            }
        }
    }

    private fun removeHeadFromQueue(){
        state.value?.let { state ->
            try {
                val queue = state.queue
                queue.remove() // can throw exception if empty
                this.state.value = state.copy(queue = queue)
            }catch (e: Exception){
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
                    sessionManager.onTriggerEvent(SessionEvents.Login(authToken))
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }

}





































