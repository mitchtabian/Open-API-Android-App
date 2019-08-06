package com.codingwithmitch.openapi.ui.main.account

import com.codingwithmitch.openapi.ui.main.account.state.AccountDataState
import com.codingwithmitch.openapi.ui.main.account.state.AccountViewState

interface AccountStateChangeListener {

    fun onAccountDataStateChange(accountDataState: AccountDataState)

    fun onAccountViewStateChange(accountViewState: AccountViewState)

    fun hideSoftKeyboard()

}