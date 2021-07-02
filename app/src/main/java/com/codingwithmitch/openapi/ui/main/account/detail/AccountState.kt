package com.codingwithmitch.openapi.ui.main.account.detail

import com.codingwithmitch.openapi.models.Account

data class AccountState(
    val isLoading: Boolean = false,
    val account: Account? = null,
)
