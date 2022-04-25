package com.templateapp.cloudapi.presentation.auth.registerAdmin

import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.StateMessage

data class RegisterState(
    val isRegistered: Boolean = false,
    val isLoading: Boolean = false,
    val number: String = "",
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
