package com.codingwithmitch.openapi.ui.main.account.password

data class AccountPasswordState(
    val isLoading: Boolean = false,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
)
