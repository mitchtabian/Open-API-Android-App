package com.codingwithmitch.openapi.ui

import android.util.Log
import androidx.lifecycle.*
import com.codingwithmitch.openapi.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow


@FlowPreview
@ExperimentalCoroutinesApi
abstract class BaseViewModel<ViewState> : ViewModel()
{
    val TAG: String = "AppDebug"

    private val _viewState: MutableLiveData<ViewState> = MutableLiveData()

    private val _activeStateEventTracker: DataChannelManager<ViewState>
            = object: DataChannelManager<ViewState>(){

        override fun handleNewData(data: ViewState) {
            this@BaseViewModel.handleNewData(data)
        }

    }

    val viewState: LiveData<ViewState>
        get() = _viewState

    val numActiveJobs: LiveData<Int>
            = _activeStateEventTracker.numActiveJobs

    val stateMessage: LiveData<StateMessage?>
        get() = _activeStateEventTracker.messageStack.stateMessage

    // FOR DEBUGGING
    fun getMessageStackSize(): Int{
        return _activeStateEventTracker.messageStack.size
    }

    fun setupChannel(){
        _activeStateEventTracker.setupChannel()
    }

    abstract fun handleNewData(data: ViewState)

    abstract fun setStateEvent(stateEvent: StateEvent)

    fun launchJob(
        stateEvent: StateEvent,
        jobFunction: Flow<DataState<ViewState>>
    ){
        _activeStateEventTracker.launchJob(stateEvent, jobFunction)
    }

    fun areAnyJobsActive(): Boolean{
        return _activeStateEventTracker.numActiveJobs.value?.let {
            it > 0
        }?: false
    }

    fun isJobAlreadyActive(stateEvent: StateEvent): Boolean {
        return _activeStateEventTracker.isJobAlreadyActive(stateEvent)
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

    fun clearStateMessage(index: Int = 0){
        _activeStateEventTracker.clearStateMessage(index)
    }

    open fun cancelActiveJobs(){
        if(areAnyJobsActive()){
//            Log.d(TAG, "cancel active jobs: ${getNumActiveJobs()}")
            Log.d(TAG, "cancel active jobs: ${_activeStateEventTracker.numActiveJobs.value ?: 0}")
            _activeStateEventTracker.cancelJobs()
        }
    }

    abstract fun initNewViewState(): ViewState

}



















