package com.codingwithmitch.openapi.presentation.main.account.detail

import com.codingwithmitch.openapi.business.domain.models.Account
import com.codingwithmitch.openapi.business.domain.util.Queue
import com.codingwithmitch.openapi.business.domain.util.StateMessage

data class AccountState(
    val isLoading: Boolean = false,
    val account: Account? = null,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
