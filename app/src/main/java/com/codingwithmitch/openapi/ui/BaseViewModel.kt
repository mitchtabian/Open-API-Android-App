package com.codingwithmitch.openapi.ui

import android.util.Log
import androidx.lifecycle.*
import com.codingwithmitch.openapi.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@FlowPreview
@ExperimentalCoroutinesApi
abstract class BaseViewModel<ViewState> : ViewModel()
{
    val TAG: String = "AppDebug"

    protected val dataChannel = ConflatedBroadcastChannel<DataState<ViewState>>()

    protected val _viewState: MutableLiveData<ViewState> = MutableLiveData()
    protected val _activeJobCounter: ActiveJobCounter = ActiveJobCounter()
    val messageStack = MessageStack()

    val viewState: LiveData<ViewState>
        get() = _viewState

    val numActiveJobs = _activeJobCounter.numActiveJobs

    val stateMessage: LiveData<StateMessage>
            = messageStack.stateMessage

    init {
        setupChannel()
    }

    private fun setupChannel(){
        dataChannel
            .asFlow()
            .onEach{ dataState ->
                dataState.data?.let { data ->
                    handleNewData(dataState.stateEvent, data)
                }
                dataState.stateMessage?.let { stateMessage ->
                    handleNewStateMessage(dataState.stateEvent, stateMessage)
                }
            }
            .launchIn(viewModelScope)
    }

    abstract fun handleNewData(stateEvent: StateEvent?, data: ViewState)

    abstract fun setStateEvent(stateEvent: StateEvent)

    fun handleNewStateMessage(stateEvent: StateEvent?, stateMessage: StateMessage){
        appendStateMessage(stateMessage)
        _activeJobCounter.removeJobFromCounter(stateEvent)
    }

    fun launchJob(
        stateEvent: StateEvent,
        jobFunction: Flow<DataState<ViewState>>
    ){
        if(!isJobAlreadyActive(stateEvent)){
            _activeJobCounter.addJobToCounter(stateEvent)
            jobFunction
                .onEach { dataState ->
                    offerToDataChannel(dataState)
                }
                .launchIn(viewModelScope)
        }
    }

    fun areAnyJobsActive(): Boolean{
        return _activeJobCounter.numActiveJobs.value?.let {
            it > 0
        }?: false
    }

    fun getNumActiveJobs(): Int {
        return _activeJobCounter.numActiveJobs.value ?: 0
    }

    fun isJobAlreadyActive(stateEvent: StateEvent): Boolean {
        return _activeJobCounter.isJobActive(stateEvent)
    }

    private fun offerToDataChannel(dataState: DataState<ViewState>){
        if(!dataChannel.isClosedForSend){
            dataChannel.offer(dataState)
        }
    }

    fun getCurrentViewStateOrNew(): ViewState{
        val value = viewState.value?.let{
            it
        }?: initNewViewState()
        return value
    }

    fun setViewState(viewState: ViewState){
        _viewState.value = viewState
    }

    private fun appendStateMessage(stateMessage: StateMessage) {
        messageStack.add(stateMessage)
    }

    fun clearStateMessage(index: Int = 0){
        messageStack.removeAt(index)
    }

    fun cancelActiveJobs(){
        Log.d(TAG, "cancel active jobs: ")
        if(areAnyJobsActive()){
            _activeJobCounter.clearActiveJobCounter()
            viewModelScope.cancel()
        }
    }

    abstract fun initNewViewState(): ViewState

}



















