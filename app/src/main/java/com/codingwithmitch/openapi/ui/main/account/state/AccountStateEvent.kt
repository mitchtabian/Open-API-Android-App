package com.codingwithmitch.openapi.ui.main.account.state

import com.codingwithmitch.openapi.util.StateEvent

sealed class AccountStateEvent: StateEvent {

    class GetAccountPropertiesEvent: AccountStateEvent() {

        override fun errorInfo(): String {
            return "Error retrieving account properties."
        }
    }

    data class UpdateAccountPropertiesEvent(
        val email: String,
        val username: String
    ): AccountStateEvent() {

        override fun errorInfo(): String {
            return "Error updating account properties."
        }
    }

    data class ChangePasswordEvent(
        val currentPassword: String,
        val newPassword: String,
        val confirmNewPassword: String
    ) : AccountStateEvent() {

        override fun errorInfo(): String {
            return "Error changing password."
        }
    }

    class None: AccountStateEvent() {
        override fun errorInfo(): String {
            return "None"
        }
    }
}