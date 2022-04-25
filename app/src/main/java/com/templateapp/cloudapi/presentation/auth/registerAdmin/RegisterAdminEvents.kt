package com.templateapp.cloudapi.presentation.auth.registerAdmin

import com.templateapp.cloudapi.business.domain.util.StateMessage

sealed class RegisterAdminEvents {

    object OnRegistered: RegisterAdminEvents()

    data class Error(val stateMessage: StateMessage): RegisterAdminEvents()

    object OnRemoveHeadFromQueue: RegisterAdminEvents()
}
