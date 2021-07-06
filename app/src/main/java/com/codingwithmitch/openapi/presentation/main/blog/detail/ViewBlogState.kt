package com.codingwithmitch.openapi.presentation.main.blog.detail

import com.codingwithmitch.openapi.business.domain.models.BlogPost
import com.codingwithmitch.openapi.business.domain.util.Queue
import com.codingwithmitch.openapi.business.domain.util.StateMessage

data class ViewBlogState(
    val isLoading: Boolean = false,
    val isDeleteComplete: Boolean = false,
    val blogPost: BlogPost? = null,
    val isAuthor: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
