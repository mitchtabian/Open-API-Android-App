package com.codingwithmitch.openapi.presentation.auth.forgot_password

import com.codingwithmitch.openapi.business.domain.util.StateMessage

sealed class ForgotPasswordEvents {

    object OnPasswordResetLinkSent: ForgotPasswordEvents()

    data class Error(val stateMessage: StateMessage): ForgotPasswordEvents()

    object OnRemoveHeadFromQueue: ForgotPasswordEvents()
}
