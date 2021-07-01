package com.codingwithmitch.openapi.ui.main.blog.update

import android.net.Uri
import com.codingwithmitch.openapi.models.BlogPost

data class UpdateBlogState(
    val isLoading: Boolean = false,
    val blogPost: BlogPost? = null,
    val newImageUri: Uri? = null, // Only set if the user has selected a new image
)
