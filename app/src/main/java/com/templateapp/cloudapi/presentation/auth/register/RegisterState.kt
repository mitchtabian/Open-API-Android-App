package com.templateapp.cloudapi.presentation.auth.register

import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.Register
import com.templateapp.cloudapi.business.domain.models.Role
import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.StateMessage

data class RegisterState(
    val isLoading: Boolean = false,

    val register: Account? = null,
    val roles: List<Role> = listOf(),
    val isComplete: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
