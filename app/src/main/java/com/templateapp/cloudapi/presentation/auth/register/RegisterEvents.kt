package com.templateapp.cloudapi.presentation.auth.register

import com.templateapp.cloudapi.business.domain.models.Role
import com.templateapp.cloudapi.presentation.main.account.update.UpdateAccountEvents
import com.templateapp.cloudapi.presentation.main.account.users.update.ChangeAccountEvents


sealed class RegisterEvents{

    data class Registration(
        val email: String,
        val role: String,
    ): RegisterEvents()

    data class OnUpdateEmail(
        val email: String
    ): RegisterEvents()

    data class OnUpdateRole(
        val role: Role
    ): RegisterEvents()


    object GetRoles: RegisterEvents()
    object OnUpdateComplete: RegisterEvents()
    object OnRemoveHeadFromQueue: RegisterEvents()
}
