package com.templateapp.cloudapi.presentation.main.blog.detail

import com.templateapp.cloudapi.business.domain.util.StateMessage


sealed class ViewBlogEvents {

    data class IsAuthor(val slug: String): ViewBlogEvents()

    data class GetBlog(val id: String): ViewBlogEvents()

    object Refresh: ViewBlogEvents()

    data class ConfirmBlogExistsOnServer(
        val id: String,
        val slug: String
    ): ViewBlogEvents()

    object DeleteBlog: ViewBlogEvents()

    object OnDeleteComplete: ViewBlogEvents()

    data class Error(val stateMessage: StateMessage): ViewBlogEvents()

    object OnRemoveHeadFromQueue: ViewBlogEvents()
}
