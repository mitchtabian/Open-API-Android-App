package com.codingwithmitch.openapi.ui.main.blog.update

import android.net.Uri
import com.codingwithmitch.openapi.util.StateMessage

sealed class UpdateBlogEvents {

    object Update: UpdateBlogEvents()

    data class getBlog(val pk: Int): UpdateBlogEvents()

    data class OnUpdateTitle(val title: String): UpdateBlogEvents()

    data class OnUpdateBody(val body: String): UpdateBlogEvents()

    data class OnUpdateImageUri(val uri: Uri): UpdateBlogEvents()

    data class Error(val stateMessage: StateMessage): UpdateBlogEvents()
}




