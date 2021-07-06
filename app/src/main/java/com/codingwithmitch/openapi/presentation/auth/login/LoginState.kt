package com.codingwithmitch.openapi.presentation.auth.login

import com.codingwithmitch.openapi.business.domain.util.Queue
import com.codingwithmitch.openapi.business.domain.util.StateMessage

data class LoginState(
    val isLoading: Boolean = false,
    val email: String = "",
    val password: String = "",
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
