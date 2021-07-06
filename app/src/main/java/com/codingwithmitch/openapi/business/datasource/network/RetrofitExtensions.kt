package com.codingwithmitch.openapi.api

import com.codingwithmitch.openapi.business.domain.util.*
import retrofit2.HttpException
import java.lang.Exception

fun <T> handleUseCaseException(e: Throwable): DataState<T> {
    e.printStackTrace()
    when (e) {
        is HttpException -> { // Retrofit exception
            val errorResponse = convertErrorBody(e)
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

private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        ErrorHandling.UNKNOWN_ERROR
    }
}