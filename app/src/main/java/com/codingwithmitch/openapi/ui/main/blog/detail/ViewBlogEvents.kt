package com.codingwithmitch.openapi.ui.main.blog.detail

import com.codingwithmitch.openapi.util.StateMessage


sealed class ViewBlogEvents {

    data class isAuthor(val slug: String): ViewBlogEvents()

    data class getBlog(val pk: Int): ViewBlogEvents()

    object DeleteBlog: ViewBlogEvents()

    data class Error(val stateMessage: StateMessage): ViewBlogEvents()
}
