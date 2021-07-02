package com.codingwithmitch.openapi.session

import com.codingwithmitch.openapi.models.AuthToken

sealed class SessionEvents {

    object Logout: SessionEvents()

    data class Login(
        val authToken: AuthToken
    ): SessionEvents()

    data class CheckPreviousAuthUser(
        val email: String
    ): SessionEvents()

}
