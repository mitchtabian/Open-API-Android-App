package com.templateapp.cloudapi.business.interactors.account

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_UPDATE_PASSWORD
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.GENERIC_ERROR
import com.templateapp.cloudapi.business.domain.util.SuccessHandling.Companion.SUCCESS_PASSWORD_UPDATED
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class UpdatePassword(
    private val service: OpenApiMainService,
    private val serverMsgTranslator: ServerMsgTranslator
) {
    fun execute(
        authToken: AuthToken?,
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String,
    ): Flow<DataState<Response>> = flow {
        emit(DataState.loading<Response>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }
        // Update network
        val response = service.updatePassword(
            authorization = authToken.token,
            currentPassword = currentPassword,
            newPassword = newPassword,
            confirmNewPassword = confirmNewPassword
        )

        response.error?.let{
            throw Exception(response.error)
        }?:run{
            response.response?.let{
                if(response.response != SUCCESS_PASSWORD_UPDATED)
                    throw Exception(GENERIC_ERROR)
            }
        }


        // Tell the UI it was successful
        emit(DataState.data<Response>(
            data = Response(
                message = SUCCESS_PASSWORD_UPDATED,
                uiComponentType = UIComponentType.None(),
                messageType = MessageType.Success()
            ),
            response = null
        ))
    }.catch { e ->
        emit(handleUseCaseException(e, serverMsgTranslator))
    }
}
