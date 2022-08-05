package com.templateapp.cloudapi.presentation.main.devices

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

import kotlinx.coroutines.*
import androidx.lifecycle.viewModelScope
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.interactors.account.GetAccount
import com.templateapp.cloudapi.business.interactors.account.GetAccountFromCache
import com.templateapp.cloudapi.business.interactors.account.GetAllUsers
import com.templateapp.cloudapi.business.interactors.account.UpdateAccount
import com.templateapp.cloudapi.business.interactors.auth.GetDevice
import com.templateapp.cloudapi.presentation.main.account.detail.AccountState
import com.templateapp.cloudapi.presentation.main.task.list.TaskEvents
import com.templateapp.cloudapi.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ManageDeviceViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val getDevices: GetDevice,
    savedStateHandle: SavedStateHandle,
): ViewModel(){

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<ManageDevicesState> = MutableLiveData(ManageDevicesState())
    init {
        onTriggerEvent(ManageDevicesEvents.GetDevice)
    }

    fun onTriggerEvent(event: ManageDevicesEvents){
        when(event){

            is ManageDevicesEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }

            is ManageDevicesEvents.GetDevice -> {
                getDevices()
            }
            is ManageDevicesEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is ManageDevicesEvents.NextPage -> {
                nextPage()
            }

        }
    }

    private fun incrementPageNumber() {
        state.value?.let { state ->
            this.state.value = state.copy(page = state.page + 1)
        }
    }

    private fun onUpdateQueryExhausted(isExhausted: Boolean) {
        state.value?.let { state ->
            this.state.value = state.copy(isQueryExhausted = isExhausted)
        }
    }


    private fun nextPage() {
        /*incrementPageNumber()
        state.value?.let { state ->
            getAllUsers.execute(
                authToken = sessionManager.state.value?.authToken,
                page = state.page,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    this.state.value = state.copy(usersList = list)

                }
                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){
                        onUpdateQueryExhausted(true)
                    }else{
                        appendToMessageQueue(stateMessage)
                    }
                }

            }.launchIn(viewModelScope)
        }*/
    }
    private fun help(){
        state.value?.let { state ->
            getDevices.execute(
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { device ->
                    this.state.value = state.copy(device = device)
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){
                        onUpdateQueryExhausted(true)
                    }else{
                        appendToMessageQueue(stateMessage)
                    }
                }

            }.launchIn(viewModelScope)
        }

    }



    fun main() = runBlocking { // this: CoroutineScope
        //repeat(100) {
            launch { // launch a new coroutine and continue
                delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
                println("World!")
                help()

            // print after delay
           // }
        } // main coroutine continues while a previous one is delayed
    }
    private fun getDevices() {
        //job.join() - if i want to wait for some coroutine to be over
        //job.cancel() - ako bude timeout ili tako nesto
        /*val Client = UDP_Client()
        Client.Message = "Your message 3"
        Client.NachrichtSenden()*/

       //main()
        /*state.value?.let { state ->
            getAllUsers.execute(
                authToken = sessionManager.state.value?.authToken,
                page = state.page,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    this.state.value = state.copy(usersList = list)
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){
                        onUpdateQueryExhausted(true)
                    }else{
                        appendToMessageQueue(stateMessage)
                    }
                }

            }.launchIn(viewModelScope)
        }*/
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

}




















