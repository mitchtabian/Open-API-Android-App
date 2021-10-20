package com.templateapp.cloudapi.presentation.main.blog.update

import android.net.Uri
import com.templateapp.cloudapi.business.domain.util.StateMessage

sealed class UpdateBlogEvents {

    object Update: UpdateBlogEvents()

    data class getBlog(val id: String): UpdateBlogEvents()

    data class OnUpdateTitle(val title: String): UpdateBlogEvents()

    data class OnUpdateBody(val body: String): UpdateBlogEvents()

    data class OnUpdateUri(val uri: Uri): UpdateBlogEvents()

    object OnUpdateComplete: UpdateBlogEvents()

    data class Error(val stateMessage: StateMessage): UpdateBlogEvents()

    object OnRemoveHeadFromQueue: UpdateBlogEvents()
}




