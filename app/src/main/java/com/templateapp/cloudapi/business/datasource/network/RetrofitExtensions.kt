package com.templateapp.cloudapi.api

import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import retrofit2.HttpException
import java.lang.Exception

fun <T> handleUseCaseException(e: Throwable, serverMsgTranslator: ServerMsgTranslator): DataState<T> {
    e.printStackTrace()
    when (e) {
        is HttpException -> { // Retrofit exception
            val errorResponse = convertErrorBody(e, serverMsgTranslator)
            return DataState.error<T>(
                response = Response(
                    message = errorResponse,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            )
        }
        else -> {
            return DataState.error<T>(
                response = Response(
                    message = e.message,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            )
        }
    }
}

private fun convertErrorBody(throwable: HttpException, serverMsgTranslator: ServerMsgTranslator): String? {
    return try {
        serverMsgTranslator.getTranslation(throwable.response()?.errorBody()?.string())
    } catch (exception: Exception) {
        ErrorHandling.UNKNOWN_ERROR
    }
}