package com.codingwithmitch.openapi.presentation.main.create_blog

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.codingwithmitch.openapi.business.domain.util.StateMessage

sealed class CreateBlogEvents {

    data class PublishBlog(
        val activity: FragmentActivity?
    ): CreateBlogEvents()

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

    object OnRemoveHeadFromQueue: CreateBlogEvents()
}










