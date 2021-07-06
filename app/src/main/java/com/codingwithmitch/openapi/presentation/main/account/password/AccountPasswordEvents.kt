package com.codingwithmitch.openapi.presentation.main.account.password


sealed class AccountPasswordEvents {

    data class ChangePassword(
        val currentPassword: String,
        val newPassword: String,
        val confirmNewPassword: String,
    ): AccountPasswordEvents()

    data class OnUpdateCurrentPassword(
        val currentPassword: String
    ): AccountPasswordEvents()

    data class OnUpdateNewPassword(
        val newPassword: String
    ): AccountPasswordEvents()

    data class OnUpdateConfirmNewPassword(
        val confirmNewPassword: String
    ): AccountPasswordEvents()

    object OnPasswordChanged: AccountPasswordEvents()

    object OnRemoveHeadFromQueue: AccountPasswordEvents()
}
