package com.templateapp.cloudapi.business.interactors.task

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.domain.util.DataState
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.GENERIC_ERROR
import com.templateapp.cloudapi.business.domain.util.MessageType
import com.templateapp.cloudapi.business.domain.util.Response
import com.templateapp.cloudapi.business.domain.util.SuccessHandling.Companion.RESPONSE_HAS_PERMISSION_TO_EDIT
import com.templateapp.cloudapi.business.domain.util.SuccessHandling.Companion.RESPONSE_NO_PERMISSION_TO_EDIT
import com.templateapp.cloudapi.business.domain.util.UIComponentType
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class IsOwnerOfTask(
    private val service: OpenApiMainService,
    private val serverMsgTranslator: ServerMsgTranslator
) {

    fun execute(
        authToken: AuthToken?,
        id: String,
    ): Flow<DataState<Boolean>> = flow {
        emit(DataState.loading<Boolean>())
        if(authToken == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        try{ // catch network exception
            val response = service.isOwnerOfTask(
                "${authToken.token}",
                id
            )
            if(response.response == RESPONSE_HAS_PERMISSION_TO_EDIT){
                emit(DataState.data(response = null, true))
            }
            else if(response.response == GENERIC_ERROR
                && response.errorMessage == RESPONSE_NO_PERMISSION_TO_EDIT){
                emit(DataState.data(
                    response = Response(
                        message = response.errorMessage,
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Success()
                    ),
                    data = false
                ))
            }
            else if(response.response == GENERIC_ERROR){
                emit(DataState.data(
                    response = Response(
                        message = response.errorMessage,
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    ),
                    data = false
                ))
            }
            else{
                emit(DataState.data(response = null, false))
            }
        }catch (e: Exception){
            emit(DataState.data(response = null, false))
        }
    }.catch { e ->
        emit(handleUseCaseException(e, serverMsgTranslator))
    }
}



























