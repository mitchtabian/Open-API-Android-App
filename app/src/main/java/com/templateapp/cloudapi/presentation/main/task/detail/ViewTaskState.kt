package com.templateapp.cloudapi.presentation.main.task.detail

import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.StateMessage

data class ViewTaskState(
    val isLoading: Boolean = false,
    val isDeleteComplete: Boolean = false,
    val task: Task? = null,
    val page: Int = 1,
    val isAuthor: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
