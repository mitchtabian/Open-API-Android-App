package com.codingwithmitch.openapi.presentation.auth.forgot_password

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingwithmitch.openapi.business.domain.util.StateMessage
import com.codingwithmitch.openapi.business.domain.util.UIComponentType
import com.codingwithmitch.openapi.business.domain.util.doesMessageAlreadyExistInQueue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel
@Inject
constructor(

): ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<ForgotPasswordState> = MutableLiveData(ForgotPasswordState())

    fun onTriggerEvent(event: ForgotPasswordEvents){
        when(event){
            is ForgotPasswordEvents.OnPasswordResetLinkSent -> {
                onPasswordResetSent()
            }
            is ForgotPasswordEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is ForgotPasswordEvents.OnRemoveHeadFromQueue -> {
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

    private fun onPasswordResetSent(){
        state.value?.let { state ->
            this.state.value = state.copy(isPasswordResetLinkSent = true)
        }
    }
}
















