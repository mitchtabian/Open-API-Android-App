package com.templateapp.cloudapi.presentation.auth.forgot_password

import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.StateMessage

data class ForgotPasswordState(
    val isLoading: Boolean = false,
    val isPasswordResetLinkSent: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
