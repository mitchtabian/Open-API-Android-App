package com.codingwithmitch.openapi.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive

class ActiveStateEventTracker : HashSet<StateEvent>(){

    private val TAG: String = "AppDebug"

    private val _numActiveJobs: MutableLiveData<Int> = MutableLiveData()

    private var channelScope: CoroutineScope? = null

    val numActiveJobs: LiveData<Int>
        get() = _numActiveJobs

    fun clearActiveStateEventCounter(){
        clear()
        syncNumActiveStateEvents()
    }

    fun addStateEvent(stateEvent: StateEvent){
        add(stateEvent)
        syncNumActiveStateEvents()
    }

    fun removeStateEvent(stateEvent: StateEvent?){
        remove(stateEvent)
        syncNumActiveStateEvents()
    }

    fun isStateEventActive(stateEvent: StateEvent): Boolean{
        return contains(stateEvent)
    }

    fun getChannelScope(): CoroutineScope {
        return channelScope?.let {
            it
        }?: setupNewChannelScope(CoroutineScope(Main))
    }

    fun setupNewChannelScope(coroutineScope: CoroutineScope): CoroutineScope{
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
        _numActiveJobs.value = this.size
    }
}