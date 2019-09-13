package com.codingwithmitch.openapi.ui.main.blog.state

import android.net.Uri
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.repository.main.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.codingwithmitch.openapi.repository.main.BlogQueryUtils.Companion.ORDER_BY_ASC_DATE_UPDATED

data class BlogViewState (

    // BlogFragment vars
    var blogFields: BlogFields = BlogFields(),

    // ViewBlogFragment vars
    var blogPost: BlogPost? = null,
    var accountProperties: AccountProperties? = null,

    // UpdateBlogFragment vars
    var updatedBlogFields: UpdatedBlogFields = UpdatedBlogFields()
)
{
    data class BlogFields(
        var searchQuery: String = "",
        var filter: String = ORDER_BY_ASC_DATE_UPDATED,
        var order: String = BLOG_ORDER_ASC,
        var page: Int = 1,
        var blogList: List<BlogPost> = ArrayList<BlogPost>(),
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false
    )

    data class UpdatedBlogFields(
        var updatedBlogTitle: String? = null,
        var updatedBlogBody: String? = null,
        var updatedImageUri: Uri? = null
    )
}
