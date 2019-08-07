package com.codingwithmitch.openapi.ui.main.account.state

import com.codingwithmitch.openapi.models.AccountProperties


data class AccountDataState(
    var error: Error? = null,
    var loading: Loading? = null,
    var successResponse: SuccessResponse? = null,
    var accountProperties: AccountProperties? = null
){
    data class Error(val errorMessage: String)
    data class Loading(val cachedData: Any?)
    data class SuccessResponse(val message: String, val useDialog: Boolean)

    companion object{

        fun error(errorMessage: String): AccountDataState{
            return AccountDataState(
                error = Error(errorMessage),
                loading = null,
                successResponse = null,
                accountProperties = null
            )
        }

        fun loading(cachedData: Any?): AccountDataState{
            return AccountDataState(
                error = null,
                loading = Loading(cachedData),
                successResponse = null,
                accountProperties = null
            )
        }

        fun successResponse(message: String, useDialog: Boolean): AccountDataState{
            return AccountDataState(
                error = null,
                loading = null,
                successResponse = SuccessResponse(message, useDialog),
                accountProperties = null
            )
        }

        fun accountProperties(accountProperties: AccountProperties): AccountDataState{
            return AccountDataState(
                error = null,
                loading = null,
                successResponse = null,
                accountProperties = accountProperties
            )
        }
    }
}















