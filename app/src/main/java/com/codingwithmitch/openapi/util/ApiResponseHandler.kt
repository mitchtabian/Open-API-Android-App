package com.codingwithmitch.openapi.util

import com.codingwithmitch.openapi.util.Constants.Companion.NETWORK_ERROR

abstract class ApiResponseHandler <ViewState, Data>(
    response: ApiResult<Data?>,
    stateEvent: StateEvent
){
    val result: DataState<ViewState> = when(response){

        is ApiResult.GenericError -> {
            DataState.error(
                response = Response(
                    message = "${stateEvent.errorInfo()}\n\nReason: ${response.errorMessage}",
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                ),
                stateEvent = stateEvent
            )
        }

        is ApiResult.NetworkError -> {
            DataState.error(
                response = Response(
                    message = "${stateEvent.errorInfo()}\n\nReason: ${NETWORK_ERROR}",
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                ),
                stateEvent = stateEvent
            )
        }

        is ApiResult.Success -> {
            if(response.value == null){
                DataState.error(
                    response = Response(
                        message = "${stateEvent.errorInfo()}\n\nReason: Data is NULL.",
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