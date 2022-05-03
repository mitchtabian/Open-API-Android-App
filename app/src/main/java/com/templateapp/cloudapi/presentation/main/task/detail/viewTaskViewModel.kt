package com.templateapp.cloudapi.presentation.main.task.detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.templateapp.cloudapi.business.domain.util.StateMessage
import com.templateapp.cloudapi.business.domain.util.SuccessHandling
import com.templateapp.cloudapi.business.domain.util.UIComponentType
import com.templateapp.cloudapi.business.domain.util.doesMessageAlreadyExistInQueue
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
class ViewTaskViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val getTaskFromCache: GetTaskFromCache,
    private val confirmTaskExistsOnServer: ConfirmTaskExistsOnServer,
    private val isOwnerOfTask: IsOwnerOfTask,
    private val deleteTask: DeleteTask,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<ViewTaskState> = MutableLiveData(ViewTaskState())

    init {
        savedStateHandle.get<String>("taskId")?.let { taskId ->
            onTriggerEvent(ViewTaskEvents.GetTask(taskId))
        }
    }

    fun onTriggerEvent(event: ViewTaskEvents){
        when(event){
            is ViewTaskEvents.GetTask -> {
                getTask(
                    event.id,
                    object: OnCompleteCallback { // Determine if task exists on server
                        override fun done() {
                            state.value?.let { state ->
                                state.task?.let { task ->
                                    onTriggerEvent(ViewTaskEvents.ConfirmTaskExistsOnServer(id = task.id))
                                }
                            }
                        }
                    }
                )
            }
            is ViewTaskEvents.ConfirmTaskExistsOnServer -> {
                confirmTaskExistsOnServer(
                    event.id,
                    object: OnCompleteCallback { // Determine if they are the author
                        override fun done() {
                            state.value?.let { state ->
                                state.task?.let { task ->
                                    onTriggerEvent(ViewTaskEvents.IsAuthor(id = task.id))
                                }
                            }
                        }
                    }
                )
            }
            is ViewTaskEvents.Refresh ->{
                refresh()
            }
            is ViewTaskEvents.IsAuthor -> {
                isOwner(event.id)
            }
            is ViewTaskEvents.DeleteTask -> {
                deleteTask()
            }
            is ViewTaskEvents.OnDeleteComplete ->{
                onDeleteComplete()
            }
            is ViewTaskEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is ViewTaskEvents.OnRemoveHeadFromQueue ->{
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
            state.task?.let { task ->
                getTask(
                    id = task.id,
                    callback = object: OnCompleteCallback{
                        override fun done() {
                            // do nothing
                        }
                    }
                )
            }
        }
    }

    private fun confirmTaskExistsOnServer(id: String, callback: OnCompleteCallback){
        state.value?.let { state ->
            confirmTaskExistsOnServer.execute(
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
        }
    }

    private fun deleteTask(){
        state.value?.let { state ->
            state.task?.let { task ->
                deleteTask.execute(
                    authToken = sessionManager.state.value?.authToken,
                    task = task
                ).onEach { dataState ->
                    this.state.value = state.copy(isLoading = dataState.isLoading)

                    dataState.data?.let { response ->
                        if(response.message == SuccessHandling.SUCCESS_TASK_DELETED){
                            onTriggerEvent(ViewTaskEvents.OnDeleteComplete)
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

    /**
     * @param callback: If the task is successfully retrieved from cache, execute to determine if the authenticated user is the owner.
     */
    private fun getTask(id: String, callback: OnCompleteCallback){
        state.value?.let { state ->
            getTaskFromCache.execute(
                id = id
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { task ->
                    this.state.value = state.copy(task = task)
                    callback.done()
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun isOwner(id: String){
        state.value?.let { state ->
            isOwnerOfTask.execute(
                authToken = sessionManager.state.value?.authToken,
                id = id,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { isAuthor ->
                    this.state.value = state.copy(isAuthor = isAuthor)
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





