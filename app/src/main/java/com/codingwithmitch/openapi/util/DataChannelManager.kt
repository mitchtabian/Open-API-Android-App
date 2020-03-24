package com.codingwithmitch.openapi.util


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@FlowPreview
@ExperimentalCoroutinesApi
abstract class DataChannelManager<ViewState> {

    private val TAG: String = "AppDebug"

    private val _activeStateEvents: HashSet<StateEvent> = HashSet()
    private val _numActiveJobs: MutableLiveData<Int> = MutableLiveData()
    private var dataChannel: ConflatedBroadcastChannel<DataState<ViewState>>? = null
    private var channelScope: CoroutineScope? = null

    val messageStack = MessageStack()

    val numActiveJobs: LiveData<Int>
        get() = _numActiveJobs

    fun setupChannel(){
        cancelJobs()
        setupNewChannelScope(CoroutineScope(Main))
        if(dataChannel != null){
            dataChannel = null
        }
        dataChannel = ConflatedBroadcastChannel()
        (dataChannel as ConflatedBroadcastChannel)
            .asFlow()
            .onEach{ dataState ->
                dataState.data?.let { data ->
                    handleNewData(data)
                    removeStateEvent(dataState.stateEvent)
                }
                dataState.stateMessage?.let { stateMessage ->
                    handleNewStateMessage(stateMessage)
                    removeStateEvent(dataState.stateEvent)
                }
            }
            .launchIn(getChannelScope())
    }

    abstract fun handleNewData(data: ViewState)

    private fun offerToDataChannel(dataState: DataState<ViewState>){
        dataChannel?.let {
            if(!it.isClosedForSend){
                it.offer(dataState)
            }
        }
    }

    fun launchJob(
        stateEvent: StateEvent,
        jobFunction: Flow<DataState<ViewState>>
    ){
        if(!isStateEventActive(stateEvent) && messageStack.size == 0){
            Log.d(TAG, "launching job ${stateEvent}")
            addStateEvent(stateEvent)
            jobFunction
                .onEach { dataState ->
                    offerToDataChannel(dataState)
                }
                .launchIn(getChannelScope())
        }
    }

    private fun handleNewStateMessage(stateMessage: StateMessage){
        appendStateMessage(stateMessage)
    }

    private fun appendStateMessage(stateMessage: StateMessage) {
        messageStack.add(stateMessage)
    }

    fun clearStateMessage(index: Int = 0){
        messageStack.removeAt(index)
    }

    private fun clearActiveStateEventCounter(){
        _activeStateEvents.clear()
        syncNumActiveStateEvents()
    }

    private fun addStateEvent(stateEvent: StateEvent){
        _activeStateEvents.add(stateEvent)
        syncNumActiveStateEvents()
    }

    private fun removeStateEvent(stateEvent: StateEvent?){
        _activeStateEvents.remove(stateEvent)
        syncNumActiveStateEvents()
    }

    fun isJobAlreadyActive(stateEvent: StateEvent): Boolean {
        return isStateEventActive(stateEvent)
    }

    private fun isStateEventActive(stateEvent: StateEvent): Boolean{
        return _activeStateEvents.contains(stateEvent)
    }

    private fun getChannelScope(): CoroutineScope {
        return channelScope?.let {
            it
        }?: setupNewChannelScope(CoroutineScope(Main))
    }

    private fun setupNewChannelScope(coroutineScope: CoroutineScope): CoroutineScope{
        channelScope = coroutineScope
        return channelScope as CoroutineScope
    }

    fun cancelJobs(){
        if(channelScope != null){
            if(channelScope?.isActive == true){
                channelScope?.cancel()
            }
        }
        clearActiveStateEventCounter()
    }

    private fun syncNumActiveStateEvents(){
        Log.d(TAG, "syncNumActiveStateEvents: ${_activeStateEvents.size}")
        for((index, job) in _activeStateEvents.withIndex()){
            Log.d(TAG, "job: $index , ${job}")
        }
        _numActiveJobs.value = _activeStateEvents.size
    }
}























