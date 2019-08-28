package com.codingwithmitch.openapi.ui.main.blog.state

import android.net.Uri
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.repository.main.BlogQueryUtils.Companion.ORDER_BY_ASC_DATE_UPDATED
import java.io.File

data class BlogViewState (

    // BlogFragment vars
    var searchQuery: String = "",
    var filter: String = ORDER_BY_ASC_DATE_UPDATED,
    var order: String = "",
    var page: Int = 1,
    var blogList: List<BlogPost> = ArrayList<BlogPost>(),
    var isQueryInProgress: Boolean = false,
    var isQueryExhausted: Boolean = false,

    // ViewBlogFragment vars
    var blogPost: BlogPost? = null,
    var accountProperties: AccountProperties? = null,

    // UpdateBlogFragment vars
    var newImageUri: Uri? = null
)

