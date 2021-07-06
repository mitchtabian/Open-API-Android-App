package com.codingwithmitch.openapi.interactors.session

import com.codingwithmitch.openapi.persistence.auth.AuthTokenDao
import com.codingwithmitch.openapi.util.DataState
import com.codingwithmitch.openapi.util.MessageType
import com.codingwithmitch.openapi.util.Response
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.SUCCESS_LOGOUT
import com.codingwithmitch.openapi.util.UIComponentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class Logout(
    private val authTokenDao: AuthTokenDao,
) {
    fun execute(): Flow<DataState<Response>> = flow {
        emit(DataState.loading<Response>())
        authTokenDao.clearTokens()
        emit(DataState.data<Response>(
            data = Response(
                message = SUCCESS_LOGOUT,
                uiComponentType = UIComponentType.Dialog(),
                messageType = MessageType.Error()
            ),
            response = null,
        ))
    }.catch{ e ->
        e.printStackTrace()
        emit(DataState.error<Response>(
            response = Response(
                message = e.message,
                uiComponentType = UIComponentType.Dialog(),
                messageType = MessageType.Error()
            )
        ))
    }
}