package com.codingwithmitch.openapi.ui.main.account.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.openapi.interactors.account.GetAccount
import com.codingwithmitch.openapi.session.SessionEvents
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.util.StateMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AccountViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val getAccount: GetAccount,
) : ViewModel() {

    val state: MutableLiveData<AccountState> = MutableLiveData(AccountState())

    init {
        onTriggerEvent(AccountEvents.GetAccount)
    }

    fun onTriggerEvent(event: AccountEvents){
        when(event){
            is AccountEvents.GetAccount -> {
                getAccount()
            }
            is AccountEvents.Logout -> {
                logout()
            }
        }
    }

    private fun getAccount(){
        state.value?.let { state ->
            getAccount.execute(
                authToken = sessionManager.state.value?.authToken,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { account ->
                    this.state.value = state.copy(account = account)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun logout(){
        sessionManager.onTriggerEvent(SessionEvents.Logout)
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        // TODO
    }
}















