package com.codingwithmitch.openapi.ui.auth.state

import com.codingwithmitch.openapi.util.StateEvent

sealed class AuthStateEvent: StateEvent {

    data class LoginAttemptEvent(
        val email: String,
        val password: String
    ): AuthStateEvent() {

        override fun errorInfo(): String {
            return "Login attempt failed."
        }

        override fun toString(): String {
            return "LoginStateEvent"
        }
    }

    data class RegisterAttemptEvent(
        val email: String,
        val username: String,
        val password: String,
        val confirm_password: String
    ): AuthStateEvent(){

        override fun errorInfo(): String {
            return "Register attempt failed."
        }

        override fun toString(): String {
            return "RegisterAttemptEvent"
        }
    }

    class CheckPreviousAuthEvent(): AuthStateEvent() {

        override fun errorInfo(): String {
            return "Error checking for previously authenticated user."
        }

        override fun toString(): String {
            return "CheckPreviousAuthEvent"
        }
    }

    class None: AuthStateEvent() {

        override fun errorInfo(): String {
            return "None"
        }

    }
}