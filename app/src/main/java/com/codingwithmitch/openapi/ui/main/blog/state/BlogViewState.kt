package com.codingwithmitch.openapi.ui.main.blog.state

import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.repository.main.BlogQueryUtils.Companion.ORDER_BY_ASC_DATE_UPDATED

data class BlogViewState (

    // BlogFragment vars
    var searchQuery: String = "",
    var filter: String = ORDER_BY_ASC_DATE_UPDATED,
    var order: String = "",
    var page: Int = 1,
    var blogList: List<BlogPost> = ArrayList(),
    var isQueryInProgress: Boolean = false,
    var isQueryExhausted: Boolean = false,

    // ViewBlogFragment vars
    var blogPost: BlogPost? = null,
    var accountProperties: AccountProperties? = null
)

