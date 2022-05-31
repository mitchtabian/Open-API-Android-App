package com.templateapp.cloudapi.presentation.auth.register


sealed class RegisterEvents{

    data class Register(
        val email: String
    ): RegisterEvents()

    data class OnUpdateEmail(
        val email: String
    ): RegisterEvents()

    object OnRemoveHeadFromQueue: RegisterEvents()
}
