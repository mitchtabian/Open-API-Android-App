package com.codingwithmitch.openapi.ui.main.blog.viewmodel

import android.net.Uri
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistence.BlogQueryUtils.Companion.BLOG_FILTER_DATE_UPDATED
import com.codingwithmitch.openapi.persistence.BlogQueryUtils.Companion.BLOG_ORDER_DESC
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.getFilter(): String {
    return getCurrentViewStateOrNew().let {
        it.blogFields.filter
    }?: BLOG_FILTER_DATE_UPDATED
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.getOrder(): String {
    return getCurrentViewStateOrNew().let {
        it.blogFields.order
    }?: BLOG_ORDER_DESC
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.getSearchQuery(): String {
    return getCurrentViewStateOrNew().let {
        it.blogFields.searchQuery
    }?: return ""
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.getPage(): Int{
    return getCurrentViewStateOrNew().let {
        it.blogFields.page
    }?: return 1
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.getSlug(): String{
    getCurrentViewStateOrNew().let {
        it.viewBlogFields.blogPost?.let {
            return it.slug
        }
    }
    return ""
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.isAuthorOfBlogPost(): Boolean{
    return getCurrentViewStateOrNew().viewBlogFields.isAuthorOfBlogPost ?: false
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.getBlogPost(): BlogPost {
    getCurrentViewStateOrNew().let {
        return it.viewBlogFields.blogPost?.let {
            return it
        }?: getDummyBlogPost()
    }
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.getDummyBlogPost(): BlogPost{
    return BlogPost(-1, "" , "", "", "", 1, "")
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.getUpdatedBlogUri(): Uri? {
    getCurrentViewStateOrNew().let {
        it.updatedBlogFields.updatedImageUri?.let {
            return it
        }
    }
    return null
}








