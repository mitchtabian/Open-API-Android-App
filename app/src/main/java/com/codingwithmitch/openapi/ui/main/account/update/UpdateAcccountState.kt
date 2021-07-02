package com.codingwithmitch.openapi.ui.main.account.update

import com.codingwithmitch.openapi.models.Account

data class UpdateAccountState(
    val isLoading: Boolean = false,
    val account: Account? = null,
)
