package com.codingwithmitch.openapi.ui.auth.login

data class LoginState(
    val isLoading: Boolean = false,
    val email: String = "",
    val password: String = "",
)
