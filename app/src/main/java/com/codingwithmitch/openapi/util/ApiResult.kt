package com.codingwithmitch.openapi.util

/**
 * Reference: https://medium.com/@douglas.iacovelli/how-to-handle-errors-with-retrofit-and-coroutines-33e7492a912
 */
sealed class ApiResult<out T> {
    data class Success<out T>(val value: T): ApiResult<T>()

    data class GenericError(
        val code: Int? = null,
        val errorMessage: String? = null
    ): ApiResult<Nothing>()

    object NetworkError: ApiResult<Nothing>()
}


