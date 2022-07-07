package com.templateapp.cloudapi.presentation.main.task.list

import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.StateMessage
import com.templateapp.cloudapi.presentation.main.task.list.TaskFilterOptions.*
import com.templateapp.cloudapi.presentation.main.task.list.TaskOrderOptions.*

data class TaskState(
    val isLoading: Boolean = false,
    val tasksList: List<Task> = listOf(),
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val filter: TaskFilterOptions = DATE_CREATED,
    val order: TaskOrderOptions = ASC,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),

)
