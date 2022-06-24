package com.templateapp.cloudapi.presentation.auth.register

import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.StateMessage

data class RegisterState(
    val isLoading: Boolean = false,
    val email: String = "",

    val isComplete: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
