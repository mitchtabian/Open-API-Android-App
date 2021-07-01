package com.codingwithmitch.openapi.ui.main.blog.list

import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.ui.main.blog.list.BlogFilterOptions.*
import com.codingwithmitch.openapi.ui.main.blog.list.BlogOrderOptions.*

data class BlogState(
    val isLoading: Boolean = false,
    val blogList: List<BlogPost> = listOf(),
    val query: String = "",
    val page: Int = 1,
    val filter: BlogFilterOptions = DATE_UPDATED,
    val order: BlogOrderOptions = DESC,
)
