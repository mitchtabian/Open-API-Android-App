package com.codingwithmitch.openapi.presentation.main.blog.update

import android.net.Uri
import com.codingwithmitch.openapi.business.domain.models.BlogPost
import com.codingwithmitch.openapi.business.domain.util.Queue
import com.codingwithmitch.openapi.business.domain.util.StateMessage

data class UpdateBlogState(
    val isLoading: Boolean = false,
    val isUpdateComplete: Boolean = false,
    val blogPost: BlogPost? = null,
    val newImageUri: Uri? = null, // Only set if the user has selected a new image
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
