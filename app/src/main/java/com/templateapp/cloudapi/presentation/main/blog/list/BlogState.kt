package com.templateapp.cloudapi.presentation.main.blog.list

import com.templateapp.cloudapi.business.domain.models.BlogPost
import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.StateMessage
import com.templateapp.cloudapi.presentation.main.blog.list.BlogFilterOptions.*
import com.templateapp.cloudapi.presentation.main.blog.list.BlogOrderOptions.*

data class BlogState(
    val isLoading: Boolean = false,
    val blogList: List<BlogPost> = listOf(),
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val filter: BlogFilterOptions = DATE_CREATED,
    val order: BlogOrderOptions = ASC,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
