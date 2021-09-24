package com.templateapp.cloudapi.presentation.main.blog.detail

import com.templateapp.cloudapi.business.domain.models.BlogPost
import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.StateMessage

data class ViewBlogState(
    val isLoading: Boolean = false,
    val isDeleteComplete: Boolean = false,
    val blogPost: BlogPost? = null,
    val isAuthor: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
