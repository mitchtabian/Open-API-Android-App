package com.codingwithmitch.openapi.ui.main.account

import com.codingwithmitch.openapi.ui.main.account.state.AccountDataState

/**
 * Listener for sending AccountDataState updates to MainActivity
 */
interface AccountStateChangeListener {

    fun onAccountDataStateChange(accountDataState: AccountDataState)

    fun hideSoftKeyboard()

}