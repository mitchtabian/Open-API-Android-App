package com.codingwithmitch.openapi.presentation.main.blog.detail

import com.codingwithmitch.openapi.business.domain.util.StateMessage


sealed class ViewBlogEvents {

    data class isAuthor(val slug: String): ViewBlogEvents()

    data class getBlog(val pk: Int): ViewBlogEvents()

    object DeleteBlog: ViewBlogEvents()

    data class Error(val stateMessage: StateMessage): ViewBlogEvents()

    object OnRemoveHeadFromQueue: ViewBlogEvents()
}
