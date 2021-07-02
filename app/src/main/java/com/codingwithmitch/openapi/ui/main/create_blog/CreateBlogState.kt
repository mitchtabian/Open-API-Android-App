package com.codingwithmitch.openapi.ui.main.create_blog

import android.net.Uri

data class CreateBlogState(
    val isLoading: Boolean = false,
    val title: String = "",
    val body: String = "",
    val uri: Uri? = null,
    val onPublishSuccess: Boolean = false,
)

