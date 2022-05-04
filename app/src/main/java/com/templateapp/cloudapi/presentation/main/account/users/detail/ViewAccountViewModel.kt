package com.templateapp.cloudapi.presentation.main.account.users.detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.templateapp.cloudapi.business.domain.util.StateMessage
import com.templateapp.cloudapi.business.domain.util.SuccessHandling
import com.templateapp.cloudapi.business.domain.util.UIComponentType
import com.templateapp.cloudapi.business.domain.util.doesMessageAlreadyExistInQueue
import com.templateapp.cloudapi.business.interactors.account.GetAccountFromCache
import com.templateapp.cloudapi.business.interactors.task.ConfirmTaskExistsOnServer
import com.templateapp.cloudapi.business.interactors.task.DeleteTask
import com.templateapp.cloudapi.business.interactors.task.GetTaskFromCache
import com.templateapp.cloudapi.business.interactors.task.IsOwnerOfTask
import com.templateapp.cloudapi.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

const val SHOULD_REFRESH = "should_refresh"

@HiltViewModel
class ViewAccountViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val getAccountFromCache: GetAccountFromCache,
    //private val confirmAccountExistsOnServer: ConfirmAccountExistsOnServer,
    //private val deleteAccount: DeleteAccount,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<ViewAccountState> = MutableLiveData(ViewAccountState())

    init {
        savedStateHandle.get<String>("accountId")?.let { accountId ->
            onTriggerEvent(ViewAccountEvents.GetAccount(accountId))
        }
    }

    fun onTriggerEvent(event: ViewAccountEvents){
        when(event){
            is ViewAccountEvents.GetAccount -> {
                getAccount(
                    event.id,
                    object: OnCompleteCallback { // Determine if task exists on server
                        override fun done() {
                            state.value?.let { state ->
                                state.account?.let { account ->
                                    onTriggerEvent(ViewAccountEvents.ConfirmAccountExistsOnServer(id = account._id))
                                }
                            }
                        }
                    }
                )
            }
            is ViewAccountEvents.ConfirmAccountExistsOnServer -> {
                confirmAccountExistsOnServer(
                    event.id,
                    object: OnCompleteCallback { // Determine if they are the author
                        override fun done() {
                            state.value?.let { state ->
                                state.account?.let { account ->
                                   // onTriggerEvent(ViewAccountEvents.IsAuthor(id = task.id))
                                }
                            }
                        }
                    }
                )
            }
            is ViewAccountEvents.Refresh ->{
                refresh()
            }

            is ViewAccountEvents.DeleteAccount -> {
                deleteAccount()
            }
            is ViewAccountEvents.OnDeleteComplete ->{
                onDeleteComplete()
            }
            is ViewAccountEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is ViewAccountEvents.OnRemoveHeadFromQueue ->{
                removeHeadFromQueue()
            }
        }
    }

    private fun onDeleteComplete() {
        state.value?.let { state ->
            this.state.value = state.copy(isDeleteComplete = true)
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

    private fun refresh(){
        state.value?.let { state ->
            state.account?.let { account ->
                getAccount(
                    id = account._id,
                    callback = object: OnCompleteCallback{
                        override fun done() {
                            // do nothing
                        }
                    }
                )
            }
        }
    }

    private fun confirmAccountExistsOnServer(id: String, callback: OnCompleteCallback){
       /* state.value?.let { state ->
            confirmAccountExistsOnServer.execute(
                authToken = sessionManager.state.value?.authToken,
                id = id,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { response ->
                    if(response.message == SuccessHandling.SUCCESS_TASK_DOES_NOT_EXIST_IN_CACHE
                        || response.message == SuccessHandling.SUCCESS_TASK_EXISTS_ON_SERVER
                    ){
                        // Task exists in cache and on server. All is good.
                        callback.done()
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
        }*/
    }

    private fun deleteAccount(){
        /*state.value?.let { state ->
            state.task?.let { task ->
                deleteTask.execute(
                    authToken = sessionManager.state.value?.authToken,
                    task = task
                ).onEach { dataState ->
                    this.state.value = state.copy(isLoading = dataState.isLoading)

                    dataState.data?.let { response ->
                        if(response.message == SuccessHandling.SUCCESS_TASK_DELETED){
                            onTriggerEvent(ViewAccountEvents.OnDeleteComplete)
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
        }*/
    }

    /**
     * @param callback: If the task is successfully retrieved from cache, execute to determine if the authenticated user is the owner.
     */
    private fun getAccount(id: String, callback: OnCompleteCallback){
        state.value?.let { state ->
            getAccountFromCache.execute(
                _id = id
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { account ->
                    this.state.value = state.copy(account = account)
                    callback.done()
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }


}


interface OnCompleteCallback {
    fun done()
}

















