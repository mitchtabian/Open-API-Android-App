package com.templateapp.cloudapi.business.interactors.account

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.datasource.cache.account.AccountDao
import com.templateapp.cloudapi.business.domain.models.Role
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.GENERIC_ERROR
import com.templateapp.cloudapi.business.domain.util.SuccessHandling.Companion.SUCCESS_ACCOUNT_UPDATED
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class ChangeAccount(
    private val service: OpenApiMainService,
    private val cache: AccountDao,
    private val serverMsgTranslator: ServerMsgTranslator
) {
    fun execute(
        authToken: AuthToken?,
        _id: String?,
        email: String,
        name: String,
        age: Int,
        enabled: Boolean,
        role: String,
        initEmail: String,
        initName: String
    ): Flow<DataState<Response>> = flow {
        emit(DataState.loading<Response>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }
        if(_id == null){
            throw Exception(ErrorHandling.ERROR_PK_INVALID)
        }

        // Update network
        val response = service.changeAccount(
            authorization = authToken.token,
            email = email,
            name = name,
            age = age,
            enabled = enabled,
            role = role,
            initEmail = initEmail,
            initName = initName
        )

        response.response?.let {
            if(response.response != SUCCESS_ACCOUNT_UPDATED){

                throw Exception(ErrorHandling.ERROR_UPDATE_ACCOUNT)
            }
        }?:run{
            throw Exception(ErrorHandling.ERROR_UPDATE_ACCOUNT)
        }


        // update cache
        cache.changeAccount(
            id = _id,
            email = email,
            name = name,
            age = age,
            enabled = enabled,
            role = role
        )

        // Tell the UI it was successful
        emit(DataState.data<Response>(
            data = Response(
                message = SUCCESS_ACCOUNT_UPDATED,
                uiComponentType = UIComponentType.Toast(),
                messageType = MessageType.Success()
            ),
            response = null
        ))
    }.catch { e ->
        emit(handleUseCaseException(e, serverMsgTranslator))
    }
}
