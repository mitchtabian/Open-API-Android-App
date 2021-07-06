package com.codingwithmitch.openapi.presentation.auth.forgot_password

import com.codingwithmitch.openapi.business.domain.util.Queue
import com.codingwithmitch.openapi.business.domain.util.StateMessage

data class ForgotPasswordState(
    val isLoading: Boolean = false,
    val isPasswordResetLinkSent: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
