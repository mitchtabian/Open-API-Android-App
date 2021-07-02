package com.codingwithmitch.openapi.ui.main.create_blog

import android.net.Uri
import com.codingwithmitch.openapi.util.StateMessage

sealed class CreateBlogEvents {

    object PublishBlog: CreateBlogEvents()

    data class OnUpdateTitle(
        val title: String,
    ): CreateBlogEvents()

    data class OnUpdateBody(
        val body: String,
    ): CreateBlogEvents()

    data class OnUpdateUri(
        val uri: Uri,
    ): CreateBlogEvents()


    object OnPublishSuccess: CreateBlogEvents()

    data class Error(val stateMessage: StateMessage): CreateBlogEvents()
}










