package com.codingwithmitch.openapi.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState


abstract class BaseViewModel<StateEvent, ViewState> : ViewModel()
{

    val TAG: String = "AppDebug"

    protected val _stateEvent: MutableLiveData<StateEvent> = MutableLiveData()
    protected val _viewState: MutableLiveData<ViewState> = MutableLiveData()

    val viewState: LiveData<ViewState>
        get() = _viewState

    val dataState: LiveData<DataState<ViewState>> = Transformations
        .switchMap(_stateEvent){stateEvent ->
            stateEvent?.let {
                Log.d(TAG, "BaseViewModel: detected new state event: ${stateEvent}")
                handleStateEvent(stateEvent)
            }
        }

    fun setStateEvent(event: StateEvent){
        Log.d(TAG, "setStateEvent: setting new state event: ${event}")
        val state: StateEvent
        state = event
        _stateEvent.value = state
    }

    abstract fun handleStateEvent(stateEvent: StateEvent): LiveData<DataState<ViewState>>

}













































