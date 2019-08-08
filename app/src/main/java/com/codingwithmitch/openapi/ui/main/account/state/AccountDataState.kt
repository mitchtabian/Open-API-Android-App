package com.codingwithmitch.openapi.ui.main.account.state

import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.util.Loading
import com.codingwithmitch.openapi.util.StateError
import com.codingwithmitch.openapi.util.SuccessResponse


data class AccountDataState(
    var error: StateError? = null,
    var loading: Loading? = null,
    var success: SuccessResponse? = null,
    var accountProperties: AccountProperties? = null
){

    companion object{

        fun error(errorMessage: String, useDialog: Boolean): AccountDataState{
            return AccountDataState(
                error = StateError(errorMessage,  useDialog),
                loading = null,
                success = null,
                accountProperties = null
            )
        }

        fun loading(
            cachedAccountProperties: AccountProperties? = null
        ): AccountDataState{
            return AccountDataState(
                error = null,
                loading = Loading(),
                success = null,
                accountProperties = cachedAccountProperties
            )
        }

        fun success(message: String?, useDialog: Boolean): AccountDataState{
            return AccountDataState(
                error = null,
                loading = null,
                success = SuccessResponse(message, useDialog),
                accountProperties = null
            )
        }

        fun accountProperties(accountProperties: AccountProperties): AccountDataState{
            return AccountDataState(
                error = null,
                loading = null,
                success = null,
                accountProperties = accountProperties
            )
        }
    }
}















