package com.codingwithmitch.openapi.presentation.main.account.update

import com.codingwithmitch.openapi.business.domain.models.Account
import com.codingwithmitch.openapi.business.domain.util.Queue
import com.codingwithmitch.openapi.business.domain.util.StateMessage

data class UpdateAccountState(
    val isLoading: Boolean = false,
    val isUpdateComplete: Boolean = false,
    val account: Account? = null,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
