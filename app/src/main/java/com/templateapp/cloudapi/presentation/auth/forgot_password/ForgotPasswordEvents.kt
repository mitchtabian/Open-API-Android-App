package com.templateapp.cloudapi.presentation.auth.forgot_password

import com.templateapp.cloudapi.business.domain.util.StateMessage

sealed class ForgotPasswordEvents {

    object OnPasswordResetLinkSent: ForgotPasswordEvents()

    data class Error(val stateMessage: StateMessage): ForgotPasswordEvents()

    object OnRemoveHeadFromQueue: ForgotPasswordEvents()
}
