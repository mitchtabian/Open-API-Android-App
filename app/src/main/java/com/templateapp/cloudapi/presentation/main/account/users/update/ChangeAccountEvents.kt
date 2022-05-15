package com.templateapp.cloudapi.presentation.main.account.users.update

import com.templateapp.cloudapi.business.domain.models.Role
import com.templateapp.cloudapi.presentation.main.account.users.ManageUsersEvents


sealed class ChangeAccountEvents{

    data class GetAccountFromCache(
        val _id: String
    ): ChangeAccountEvents()

    data class Update(
        val email: String,
        val username: String,
        val age: Int,
        val enabled: Boolean,
        val role: String,
        val initEmail: String,
        val initName: String
    ): ChangeAccountEvents()

    data class OnUpdateEmail(
        val email: String
    ): ChangeAccountEvents()

    data class OnUpdateRole(
        val role: Role
    ): ChangeAccountEvents()

    data class OnUpdateEnabled(
        val enabled: Boolean
    ): ChangeAccountEvents()

    data class OnUpdateUsername(
        val username: String
    ): ChangeAccountEvents()

    data class OnUpdateAge(
        val age: Int
    ): ChangeAccountEvents()

    object GetRoles: ChangeAccountEvents()
    object OnUpdateComplete: ChangeAccountEvents()

    object OnRemoveHeadFromQueue: ChangeAccountEvents()
}
