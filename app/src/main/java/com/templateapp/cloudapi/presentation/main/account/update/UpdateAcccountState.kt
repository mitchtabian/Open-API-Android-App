package com.templateapp.cloudapi.presentation.main.account.update

import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.StateMessage

data class UpdateAccountState(
    val isLoading: Boolean = false,
    val isUpdateComplete: Boolean = false,
    val account: Account? = null,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
