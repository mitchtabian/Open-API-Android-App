package com.codingwithmitch.openapi.presentation.main.account.update

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.openapi.business.domain.util.StateMessage
import com.codingwithmitch.openapi.business.domain.util.SuccessHandling
import com.codingwithmitch.openapi.business.domain.util.UIComponentType
import com.codingwithmitch.openapi.business.domain.util.doesMessageAlreadyExistInQueue
import com.codingwithmitch.openapi.business.interactors.account.GetAccountFromCache
import com.codingwithmitch.openapi.business.interactors.account.UpdateAccount
import com.codingwithmitch.openapi.presentation.session.SessionManager
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

    private val TAG: String = "AppDebug"

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
            is UpdateAccountEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
            is UpdateAccountEvents.OnUpdateComplete -> {
                onUpdateComplete()
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

    private fun onUpdateComplete(){
        state.value?.let { state ->
            this.state.value = state.copy(isUpdateComplete = true)
        }
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
                authToken = sessionManager.state.value?.authToken,
                pk = sessionManager.state.value?.authToken?.accountPk,
                email = email,
                username = username,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { response ->
                    if(response.message == SuccessHandling.SUCCESS_ACCOUNT_UPDATED){
                        onTriggerEvent(UpdateAccountEvents.OnUpdateComplete)
                    }else{
                        appendToMessageQueue(
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




















