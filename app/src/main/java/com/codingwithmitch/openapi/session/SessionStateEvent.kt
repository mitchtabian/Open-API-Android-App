package com.codingwithmitch.openapi.session

import com.codingwithmitch.openapi.models.AuthToken


sealed class SessionStateEvent {

    class Logout(): SessionStateEvent()

    data class Login(
        val authToken: AuthToken
    ): SessionStateEvent()
}











