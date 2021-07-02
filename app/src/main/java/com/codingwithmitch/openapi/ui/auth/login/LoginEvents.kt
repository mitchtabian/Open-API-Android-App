package com.codingwithmitch.openapi.ui.auth.login

sealed class LoginEvents{

    data class Login(
        val email: String,
        val password: String
    ): LoginEvents()

    data class OnUpdateEmail(
        val email: String
    ): LoginEvents()

    data class OnUpdatePassword(
        val password: String
    ): LoginEvents()
}
