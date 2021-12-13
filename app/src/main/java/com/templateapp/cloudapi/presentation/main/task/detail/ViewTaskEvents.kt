package com.templateapp.cloudapi.presentation.main.task.detail

import com.templateapp.cloudapi.business.domain.util.StateMessage


sealed class ViewTaskEvents {

    data class IsAuthor(val id: String): ViewTaskEvents()

    data class GetTask(val id: String): ViewTaskEvents()

    object Refresh: ViewTaskEvents()

    data class ConfirmTaskExistsOnServer(
        val id: String
    ): ViewTaskEvents()

    object DeleteTask: ViewTaskEvents()

    object OnDeleteComplete: ViewTaskEvents()

    data class Error(val stateMessage: StateMessage): ViewTaskEvents()

    object OnRemoveHeadFromQueue: ViewTaskEvents()
}
