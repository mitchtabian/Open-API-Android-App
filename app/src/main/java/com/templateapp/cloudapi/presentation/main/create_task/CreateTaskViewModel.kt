package com.templateapp.cloudapi.presentation.main.create_task

import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.domain.util.SuccessHandling.Companion.SUCCESS_TASK_CREATED
import com.templateapp.cloudapi.business.interactors.task.PublishTask
import com.templateapp.cloudapi.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class CreateTaskViewModel
@Inject
constructor(
    private val publishTask: PublishTask,
    private val sessionManager: SessionManager
    //private val baseApplication: BaseApplication
): ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<CreateTaskState> = MutableLiveData(CreateTaskState())

    //@Inject
    //lateinit var baseApplication: BaseApplication

    fun onTriggerEvent(event: CreateTaskEvents){
        when(event){
            is CreateTaskEvents.OnUpdateTitle -> {
                onUpdateTitle(event.title)
            }
            is CreateTaskEvents.OnUpdateBody -> {
                onUpdateBody(event.body)
            }
            is CreateTaskEvents.OnUpdateUri -> {
                onUpdateUri(event.uri)
            }
            is CreateTaskEvents.PublishTask -> {
                publishTask(event.activity)
            }
            is CreateTaskEvents.OnPublishSuccess -> {
                onPublishSuccess()
            }
            is CreateTaskEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is CreateTaskEvents.OnRemoveHeadFromQueue ->{
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

    // call after successfully publishing
    private fun clearNewTaskFields(){
        onUpdateTitle("")
        onUpdateBody("")
        onUpdateUri(null)
    }

    private fun onPublishSuccess(){
        clearNewTaskFields()
        state.value?.let { state ->
            this.state.value = state.copy(onPublishSuccess = true)
        }
    }

    private fun onUpdateUri(uri: Uri?){
        state.value?.let { state ->
            this.state.value = state.copy(uri = uri)
        }
    }

    private fun onUpdateTitle(title: String){
        state.value?.let { state ->
            this.state.value = state.copy(title = title)
        }
    }

    private fun onUpdateBody(body: String){
        state.value?.let { state ->
            this.state.value = state.copy(body = body)
        }
    }

    private fun publishTask(activity: FragmentActivity?){
        state.value?.let { state ->
            val title = RequestBody.create(
                MediaType.parse("text/plain"),
                state.title
            )
            val body = RequestBody.create(
                MediaType.parse("text/plain"),
                state.body
            )
            val completed = state.completed

            if(state.uri == null){
                onTriggerEvent(CreateTaskEvents.Error(
                    stateMessage = StateMessage(
                        response = Response(
                            message = ErrorHandling.ERROR_MUST_SELECT_IMAGE,
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        )
                    )
                ))
            }
            else{
                var multipartBody: MultipartBody.Part? = null
                state.uri?.let { contentFilePath ->
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

                if(multipartBody != null){
                    publishTask.execute(
                        authToken = sessionManager.state.value?.authToken,
                        title = title,
                        body = body,
                        completed = state.completed,
                        image = multipartBody,
                    ).onEach { dataState ->
                        this.state.value = state.copy(isLoading = dataState.isLoading)

                        dataState.data?.let { response ->
                            if(response.message == SUCCESS_TASK_CREATED){
                                onTriggerEvent(CreateTaskEvents.OnPublishSuccess)
                            }else{
                                appendToMessageQueue(
                                    stateMessage = StateMessage(response)
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
    }
}





