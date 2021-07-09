package com.codingwithmitch.openapi.presentation.main.account.password

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.openapi.business.domain.util.StateMessage
import com.codingwithmitch.openapi.business.domain.util.SuccessHandling
import com.codingwithmitch.openapi.business.domain.util.UIComponentType
import com.codingwithmitch.openapi.business.domain.util.doesMessageAlreadyExistInQueue
import com.codingwithmitch.openapi.business.interactors.account.UpdatePassword
import com.codingwithmitch.openapi.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AccountPasswordViewModel
@Inject
constructor(
    private val updatePassword: UpdatePassword,
    private val sessionManager: SessionManager,
): ViewModel(){

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<AccountPasswordState> = MutableLiveData(AccountPasswordState())

    fun onTriggerEvent(event: AccountPasswordEvents){
        when(event){
            is AccountPasswordEvents.ChangePassword -> {
                changePassword()
            }
            is AccountPasswordEvents.OnUpdateCurrentPassword -> {
                onUpdateCurrentPassword(event.currentPassword)
            }
            is AccountPasswordEvents.OnUpdateNewPassword -> {
                onUpdateNewPassword(event.newPassword)
            }
            is AccountPasswordEvents.OnUpdateConfirmNewPassword -> {
                onUpdateConfirmNewPassword(event.confirmNewPassword)
            }
            is AccountPasswordEvents.OnRemoveHeadFromQueue ->{
                removeHeadFromQueue()
            }
            is AccountPasswordEvents.OnPasswordChanged ->{
                onPasswordChangeComplete()
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

    private fun onPasswordChangeComplete(){
        state.value?.let { state ->
            this.state.value = state.copy(isPasswordChangeComplete = true)
        }
    }

    private fun onUpdateCurrentPassword(currentPassword: String) {
        state.value?.let { state ->
            this.state.value = state.copy(currentPassword = currentPassword)
        }
    }

    private fun onUpdateNewPassword(newPassword: String) {
        state.value?.let { state ->
            this.state.value = state.copy(newPassword = newPassword)
        }
    }

    private fun onUpdateConfirmNewPassword(confirmNewPassword: String) {
        state.value?.let { state ->
            this.state.value = state.copy(confirmNewPassword = confirmNewPassword)
        }
    }

    private fun changePassword() {
        // TODO("Should perform some simple validation client-side here")
        state.value?.let { state ->
            updatePassword.execute(
                authToken = sessionManager.state.value?.authToken,
                currentPassword = state.currentPassword,
                newPassword = state.newPassword,
                confirmNewPassword = state.confirmNewPassword
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { response ->
                    if(response.message == SuccessHandling.SUCCESS_PASSWORD_UPDATED){
                        onTriggerEvent(AccountPasswordEvents.OnPasswordChanged)
                    }else{
                        appendToMessageQueue( // Tell the UI it was updated
                            stateMessage = StateMessage(
                                response = response
                            )
                        )
                    }
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }

    }
}













