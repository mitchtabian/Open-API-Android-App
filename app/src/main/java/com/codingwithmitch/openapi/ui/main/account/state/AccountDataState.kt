package com.codingwithmitch.openapi.ui.main.account.state

import com.codingwithmitch.openapi.models.AccountProperties


sealed class AccountDataState{
    data class Error(val errorMessage: String): AccountDataState()
    data class Data(
        val accountProperties: AccountProperties?): AccountDataState() // return null to hide loading
    data class Loading(val accountProperties: AccountProperties?): AccountDataState()
}