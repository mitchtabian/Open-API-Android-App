package com.codingwithmitch.openapi.ui.main.account.password

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.openapi.interactors.account.UpdatePassword
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.util.StateMessage
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

    val state: MutableLiveData<AccountPasswordState> = MutableLiveData()

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
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        // TODO
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
                    appendToMessageQueue( // Tell the UI it was updated
                        stateMessage = StateMessage(
                            response = response
                        )
                    )
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }

    }
}













