package com.codingwithmitch.openapi.presentation.main.blog.detail

import com.codingwithmitch.openapi.business.domain.util.StateMessage


sealed class ViewBlogEvents {

    data class IsAuthor(val slug: String): ViewBlogEvents()

    data class GetBlog(val pk: Int): ViewBlogEvents()

    object Refresh: ViewBlogEvents()

    data class ConfirmBlogExistsOnServer(
        val pk: Int,
        val slug: String
    ): ViewBlogEvents()

    object DeleteBlog: ViewBlogEvents()

    object OnDeleteComplete: ViewBlogEvents()

    data class Error(val stateMessage: StateMessage): ViewBlogEvents()

    object OnRemoveHeadFromQueue: ViewBlogEvents()
}
