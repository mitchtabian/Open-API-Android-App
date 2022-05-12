package com.templateapp.cloudapi.presentation.main.account.users.update

import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.Role
import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.StateMessage

data class ChangeAccountState(
    val isLoading: Boolean = false,
    val isUpdateComplete: Boolean = false,
    val account: Account? = null,
    val roles: List<Role> = listOf(),
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
