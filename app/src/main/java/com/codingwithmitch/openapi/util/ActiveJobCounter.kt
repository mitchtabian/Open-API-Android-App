package com.codingwithmitch.openapi.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ActiveJobCounter : HashSet<StateEvent>(){

    private val _numActiveJobs: MutableLiveData<Int> = MutableLiveData()

    val numActiveJobs: LiveData<Int>
        get() = _numActiveJobs

    fun clearActiveJobCounter(){
        this.clear()
        syncNumActiveJobs()
    }

    fun addJobToCounter(stateEvent: StateEvent){
        add(stateEvent)
        syncNumActiveJobs()
    }

    fun removeJobFromCounter(stateEvent: StateEvent?){
        remove(stateEvent)
        syncNumActiveJobs()
    }

    fun isJobActive(stateEvent: StateEvent): Boolean{
        return contains(stateEvent)
    }

    private fun syncNumActiveJobs(){
        _numActiveJobs.value = this.size
    }
}