package com.codingwithmitch.openapi.ui.main.blog.detail

import com.codingwithmitch.openapi.models.BlogPost

data class ViewBlogState(
    val isLoading: Boolean = false,
    val blogPost: BlogPost? = null,
    val isAuthor: Boolean = false,
)
