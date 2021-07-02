package com.codingwithmitch.openapi.ui.main.account.detail

sealed class AccountEvents{

    object GetAccount: AccountEvents()

    object Logout: AccountEvents()

}
