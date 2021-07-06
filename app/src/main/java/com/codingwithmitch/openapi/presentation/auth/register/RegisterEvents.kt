package com.codingwithmitch.openapi.presentation.auth.register


sealed class RegisterEvents{

    data class Register(
        val email: String,
        val username: String,
        val password: String,
        val confirmPassword: String,
    ): RegisterEvents()

    data class OnUpdateEmail(
        val email: String
    ): RegisterEvents()

    data class OnUpdateUsername(
        val username: String
    ): RegisterEvents()

    data class OnUpdatePassword(
        val password: String
    ): RegisterEvents()

    data class OnUpdateConfirmPassword(
        val confirmPassword: String
    ): RegisterEvents()

    object OnRemoveHeadFromQueue: RegisterEvents()
}
