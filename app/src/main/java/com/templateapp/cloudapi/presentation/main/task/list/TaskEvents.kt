package com.templateapp.cloudapi.presentation.main.task.list

import com.templateapp.cloudapi.business.domain.util.StateMessage


sealed class TaskEvents {

    object NewSearch : TaskEvents()

    object NextPage: TaskEvents()

    data class UpdateQuery(val query: String): TaskEvents()

    data class UpdateFilter(val filter: TaskFilterOptions): TaskEvents()

    data class UpdateOrder(val order: TaskOrderOptions): TaskEvents()

    object GetOrderAndFilter: TaskEvents()

    data class Error(val stateMessage: StateMessage): TaskEvents()

    object OnRemoveHeadFromQueue: TaskEvents()
}
