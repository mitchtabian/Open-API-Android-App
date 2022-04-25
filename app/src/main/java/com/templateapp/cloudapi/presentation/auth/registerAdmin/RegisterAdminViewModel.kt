package com.templateapp.cloudapi.presentation.auth.registerAdmin

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.templateapp.cloudapi.business.datasource.datastore.AppDataStore
import com.templateapp.cloudapi.business.domain.util.ErrorHandling
import com.templateapp.cloudapi.business.domain.util.StateMessage
import com.templateapp.cloudapi.business.domain.util.UIComponentType
import com.templateapp.cloudapi.business.domain.util.doesMessageAlreadyExistInQueue
import com.templateapp.cloudapi.business.interactors.account.GetAllUsers
import com.templateapp.cloudapi.business.interactors.task.GetOrderAndFilter
import com.templateapp.cloudapi.business.interactors.task.SearchTasks
import com.templateapp.cloudapi.presentation.main.task.list.TaskEvents
import com.templateapp.cloudapi.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RegisterAdminViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val getAllUsers: GetAllUsers,
): ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<RegisterState> = MutableLiveData(RegisterState())
    init {
        search()
    }
    fun onTriggerEvent(event: RegisterAdminEvents){
        when(event){
            is RegisterAdminEvents.OnRegistered -> {
                search()
            }
            is RegisterAdminEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is RegisterAdminEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }


    private fun search() {


        println("OGOGOGOGO")
        state.value?.let { state ->
            getAllUsers.execute(
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    this.state.value = state.copy(number = list)
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){
                        print("ok")
                    }else{
                        print("ne")
                    }
                }

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

    private fun onRegistered(){
        state.value?.let { state ->
            this.state.value = state.copy(isRegistered = true)
        }
    }
}
















