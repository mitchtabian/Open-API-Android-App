package com.codingwithmitch.openapi.ui.main.account

import com.codingwithmitch.openapi.ui.main.account.state.AccountDataState

interface AccountStateChangeListener {

    fun onAccountDataStateChange(accountDataState: AccountDataState)

    fun hideSoftKeyboard()

}