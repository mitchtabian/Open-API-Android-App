package com.templateapp.cloudapi.presentation.main.task.update

import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.templateapp.cloudapi.business.domain.util.StateMessage
import com.templateapp.cloudapi.business.domain.util.SuccessHandling.Companion.SUCCESS_TASK_UPDATED
import com.templateapp.cloudapi.business.domain.util.UIComponentType
import com.templateapp.cloudapi.business.domain.util.doesMessageAlreadyExistInQueue
import com.templateapp.cloudapi.business.interactors.task.GetTaskFromCache
import com.templateapp.cloudapi.business.interactors.task.UpdateTask
import com.templateapp.cloudapi.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class UpdateTaskViewModel
@Inject
constructor(
    public val sessionManager: SessionManager,
    private val getTaskFromCache: GetTaskFromCache,
    private val updateTask: UpdateTask,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<UpdateTaskState> = MutableLiveData(UpdateTaskState())

    init {
        savedStateHandle.get<String>("taskId")?.let { taskId ->
            onTriggerEvent(UpdateTaskEvents.getTask(taskId))
        }
    }

    fun onTriggerEvent(event: UpdateTaskEvents) {
        when(event){
            is UpdateTaskEvents.getTask -> {
                getTask(event.id,)
            }
            is UpdateTaskEvents.OnUpdateUri -> {
                onUpdateImageUri(event.uri)
            }
            is UpdateTaskEvents.OnUpdateTitle -> {
                onUpdateTitle(event.title)
            }
            is UpdateTaskEvents.OnUpdateBody -> {
                onUpdateBody(event.body)
            }
            is UpdateTaskEvents.Update -> {
                update(event.activity)
            }
            is UpdateTaskEvents.OnUpdateComplete ->{
                onUpdateComplete()
            }
            is UpdateTaskEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is UpdateTaskEvents.OnRemoveHeadFromQueue ->{
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

    private fun onUpdateComplete(){
        state.value?.let { state ->
            this.state.value = state.copy(isUpdateComplete = true)
        }
    }

    private fun onUpdateTitle(title: String){
        state.value?.let { state ->
            state.task?.let { task ->
                val curr = task.copy(title = title)
                this.state.value = state.copy(task = curr)
            }
        }
    }

    private fun onUpdateBody(body: String){
        state.value?.let { state ->
            state.task?.let { task ->
                val curr = task.copy(description = body)
                this.state.value = state.copy(task = curr)
            }
        }
    }

    private fun onUpdateImageUri(uri: Uri){
        state.value?.let { state ->
            this.state.value = state.copy(newImageUri = uri)
        }
    }

    private fun update(activity: FragmentActivity?){
        state.value?.let { state ->
            state.task?.let { task ->
                val title = RequestBody.create(
                    MediaType.parse("text/plain"),
                    task.title
                )
                val body = RequestBody.create(
                    MediaType.parse("text/plain"),
                    task.description
                )
                var multipartBody: MultipartBody.Part? = null
                state.newImageUri?.let { contentFilePath ->
                    val filename = contentFilePath.path?.split("/")?.lastOrNull()
                    val imageFile = activity?.contentResolver?.openInputStream(contentFilePath)

                    imageFile?.let{
                        val requestBody =
                            RequestBody.create(
                                MediaType.parse("image/*"),
                                imageFile.readBytes()
                            )
                        multipartBody = MultipartBody.Part.createFormData(
                            "image",
                            filename,
                            requestBody
                        )
                    }
                }
                updateTask.execute(
                    authToken = sessionManager.state.value?.authToken,
                    id = state.task.id,
                    completed = state.task.completed,
                    title = title,
                    description = body,
                    image = multipartBody,
                ).onEach { dataState ->
                    this.state.value = state.copy(isLoading = dataState.isLoading)

                    dataState.data?.let { response ->
                        if(response.message == SUCCESS_TASK_UPDATED){
                            onTriggerEvent(UpdateTaskEvents.OnUpdateComplete)
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

    private fun getTask(id: String){
        state.value?.let { state ->
            getTaskFromCache.execute(
                id = id
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { task ->
                    this.state.value = state.copy(task = task)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }
}


















