package com.codingwithmitch.openapi.ui.main.blog.state

import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.repository.main.BlogQueryUtils.Companion.ORDER_BY_ASC_DATE_UPDATED

data class BlogViewState (
    var searchQuery: String = "",
    var order: String = ORDER_BY_ASC_DATE_UPDATED,
    var page: Int = 1,
    var blogList: List<BlogPost> = ArrayList<BlogPost>(),
    var isQueryInProgress: Boolean = false,
    var isQueryExhausted: Boolean = false
)