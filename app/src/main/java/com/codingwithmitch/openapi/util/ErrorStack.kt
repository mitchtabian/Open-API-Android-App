package com.codingwithmitch.openapi.util

import androidx.lifecycle.MutableLiveData
import kotlinx.android.parcel.IgnoredOnParcel
import java.lang.IndexOutOfBoundsException

const val ERROR_STACK_BUNDLE_KEY = "com.codingwithmitch.openapi.util.ErrorStack"


class ErrorStack: ArrayList<StateMessage>() {

    @IgnoredOnParcel
    val stateError: MutableLiveData<StateMessage> = MutableLiveData()

    override fun addAll(elements: Collection<StateMessage>): Boolean {
        for(element in elements){
            add(element)
        }
        return true // always return true. We don't care about result bool.
    }

    override fun add(element: StateMessage): Boolean {
        if(this.size == 0){
            setStateError(errorState = element)
        }
        if(this.contains(element)){ // prevent duplicate errors added to stack
            return false
        }
        return super.add(element)
    }

    override fun removeAt(index: Int): StateMessage {
        try{
            val transaction = super.removeAt(index)
            if(this.size > 0){
                setStateError(errorState = this[0])
            }
            else{
                setStateError(null)
            }
            return transaction
        }catch (e: IndexOutOfBoundsException){
            e.printStackTrace()
        }
        return StateMessage(
            Response(
                message = "does nothing",
                uiComponentType = UIComponentType.None(),
                messageType = MessageType.None()
            )
        ) // this does nothing
    }

    private fun setStateError(errorState: StateMessage?){
        this.stateError.value = errorState
    }
}