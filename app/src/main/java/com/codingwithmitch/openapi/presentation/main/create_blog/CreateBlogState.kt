package com.codingwithmitch.openapi.presentation.main.create_blog

import android.net.Uri
import com.codingwithmitch.openapi.business.domain.util.Queue
import com.codingwithmitch.openapi.business.domain.util.StateMessage

data class CreateBlogState(
    val isLoading: Boolean = false,
    val title: String = "",
    val body: String = "",
    val uri: Uri? = null,
    val onPublishSuccess: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)

