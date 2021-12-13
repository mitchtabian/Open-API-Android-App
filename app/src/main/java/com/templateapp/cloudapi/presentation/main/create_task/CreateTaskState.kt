package com.templateapp.cloudapi.presentation.main.create_task

import android.net.Uri
import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.StateMessage

data class CreateTaskState(
    val isLoading: Boolean = false,
    val title: String = "",
    val body: String = "",
    val completed: Boolean = false,
    val uri: Uri? = null,
    val onPublishSuccess: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)

