package com.codingwithmitch.openapi.util

abstract class CacheResponseHandler <ViewState, Data>(
    response: CacheResult<Data?>,
    stateEvent: StateEvent?
){
    val result: DataState<ViewState> = when(response){

        is CacheResult.GenericError -> {
            DataState.error(
                response = Response(
                    message = "${stateEvent?.errorInfo()}\n\nReason: ${response.errorMessage}",
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                ),
                stateEvent = stateEvent
            )
        }

        is CacheResult.Success -> {
            if(response.value == null){
                DataState.error(
                    response = Response(
                        message = "${stateEvent?.errorInfo()}\n\nReason: Data is NULL.",
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    ),
                    stateEvent = stateEvent
                )
            }
            else{
                handleSuccess(resultObj = response.value)
            }
        }
    }

    abstract fun handleSuccess(resultObj: Data): DataState<ViewState>

}