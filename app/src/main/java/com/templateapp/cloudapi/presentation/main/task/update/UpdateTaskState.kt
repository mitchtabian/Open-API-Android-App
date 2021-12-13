package com.templateapp.cloudapi.presentation.main.task.update

import android.net.Uri
import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.StateMessage

data class UpdateTaskState(
    val isLoading: Boolean = false,
    val isUpdateComplete: Boolean = false,
    val task: Task? = null,
    val newImageUri: Uri? = null, // Only set if the user has selected a new image
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
