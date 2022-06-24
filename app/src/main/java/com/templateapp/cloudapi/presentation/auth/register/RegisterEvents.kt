package com.templateapp.cloudapi.presentation.auth.register

import com.templateapp.cloudapi.presentation.main.account.update.UpdateAccountEvents


sealed class RegisterEvents{

    data class Register(
        val email: String
    ): RegisterEvents()

    data class OnUpdateEmail(
        val email: String
    ): RegisterEvents()


    object OnUpdateComplete: RegisterEvents()
    object OnRemoveHeadFromQueue: RegisterEvents()
}
