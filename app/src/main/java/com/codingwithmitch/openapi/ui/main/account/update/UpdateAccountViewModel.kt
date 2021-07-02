package com.codingwithmitch.openapi.ui.main.account.update

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.openapi.interactors.account.GetAccountFromCache
import com.codingwithmitch.openapi.interactors.account.UpdateAccount
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.main.blog.detail.ViewBlogEvents
import com.codingwithmitch.openapi.util.StateMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class UpdateAccountViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val updateAccount: UpdateAccount,
    private val getAccountFromCache: GetAccountFromCache,
    private val savedStateHandle: SavedStateHandle,
): ViewModel(){

    val state: MutableLiveData<UpdateAccountState> = MutableLiveData(UpdateAccountState())

    init {
        savedStateHandle.get<Int>("accountPk")?.let { accountPk ->
            onTriggerEvent(UpdateAccountEvents.GetAccountFromCache(accountPk))
        }
    }

    fun onTriggerEvent(event: UpdateAccountEvents){
        when(event){
            is UpdateAccountEvents.OnUpdateEmail -> {
                onUpdateEmail(event.email)
            }
            is UpdateAccountEvents.OnUpdateUsername -> {
                onUpdateUsername(event.username)
            }
            is UpdateAccountEvents.GetAccountFromCache -> {
                getAccount(event.pk)
            }
            is UpdateAccountEvents.Update -> {
                update(
                    email = event.email,
                    username = event.username
                )
            }
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        // TODO
    }

    private fun onUpdateEmail(email: String){
        state.value?.let { state ->
            state.account?.let { account ->
                val new = account.copy(email = email)
                this.state.value = state.copy(account = new)
            }
        }
    }

    private fun onUpdateUsername(username: String){
        state.value?.let { state ->
            state.account?.let { account ->
                val new = account.copy(username = username)
                this.state.value = state.copy(account = new)
            }
        }
    }

    private fun getAccount(pk: Int) {
        state.value?.let { state ->
            getAccountFromCache.execute(
                pk = pk,
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

    private fun update(email: String, username: String,){
        state.value?.let { state ->
            updateAccount.execute(
                authToken = sessionManager.cachedToken.value,
                pk = sessionManager.cachedToken.value?.account_pk,
                email = email,
                username = username,
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




















