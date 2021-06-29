package com.codingwithmitch.openapi.ui.main.blog.viewmodel

import android.net.Uri
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistence.BlogQueryUtils.Companion.BLOG_FILTER_DATE_UPDATED
import com.codingwithmitch.openapi.persistence.BlogQueryUtils.Companion.BLOG_ORDER_DESC

fun BlogViewModel.getIsQueryExhausted(): Boolean {
    return getCurrentViewStateOrNew().blogFields.isQueryExhausted
        ?: false
}

fun BlogViewModel.getFilter(): String {
    return getCurrentViewStateOrNew().blogFields.filter
        ?: BLOG_FILTER_DATE_UPDATED
}

fun BlogViewModel.getOrder(): String {
    return getCurrentViewStateOrNew().blogFields.order
        ?: BLOG_ORDER_DESC
}

fun BlogViewModel.getSearchQuery(): String {
    return getCurrentViewStateOrNew().blogFields.searchQuery
        ?: return ""
}

fun BlogViewModel.getPage(): Int{
    return getCurrentViewStateOrNew().blogFields.page
        ?: return 1
}

fun BlogViewModel.getSlug(): String{
    getCurrentViewStateOrNew().let {
        it.viewBlogFields.blogPost?.let {
            return it.slug
        }
    }
    return ""
}

fun BlogViewModel.isAuthorOfBlogPost(): Boolean{
    return getCurrentViewStateOrNew().viewBlogFields.isAuthorOfBlogPost
        ?: false
}

fun BlogViewModel.getBlogPost(): BlogPost {
    getCurrentViewStateOrNew().let {
        return it.viewBlogFields.blogPost?.let {
            return it
        }?: getDummyBlogPost()
    }
}

fun BlogViewModel.getDummyBlogPost(): BlogPost{
    return BlogPost(-1, "" , "", "", "", 1, "")
}

fun BlogViewModel.getUpdatedBlogUri(): Uri? {
    getCurrentViewStateOrNew().let {
        it.updatedBlogFields.updatedImageUri?.let {
            return it
        }
    }
    return null
}






