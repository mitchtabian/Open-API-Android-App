package com.templateapp.cloudapi.presentation.main.account.users.detail

import com.templateapp.cloudapi.business.domain.util.StateMessage


sealed class ViewAccountEvents {

    data class GetAccount(val id: String): ViewAccountEvents()

    object Refresh: ViewAccountEvents()

    data class ConfirmAccountExistsOnServer(
        val id: String
    ): ViewAccountEvents()

    object DeleteAccount: ViewAccountEvents()

    object OnDeleteComplete: ViewAccountEvents()

    data class Error(val stateMessage: StateMessage): ViewAccountEvents()

    object OnRemoveHeadFromQueue: ViewAccountEvents()
}
