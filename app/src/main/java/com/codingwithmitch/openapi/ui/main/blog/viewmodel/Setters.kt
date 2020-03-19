package com.codingwithmitch.openapi.ui.main.blog.viewmodel

import android.net.Uri
import android.os.Parcelable
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.setQuery(query: String){
    val update = getCurrentViewStateOrNew()
    update.blogFields.searchQuery = query
    setViewState(update)
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.setBlogListData(blogList: List<BlogPost>){
    val update = getCurrentViewStateOrNew()
    update.blogFields.blogList = blogList
    setViewState(update)
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.setBlogPost(blogPost: BlogPost){
    val update = getCurrentViewStateOrNew()
    update.viewBlogFields.blogPost = blogPost
    setViewState(update)
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.setIsAuthorOfBlogPost(isAuthorOfBlogPost: Boolean){
    val update = getCurrentViewStateOrNew()
    update.viewBlogFields.isAuthorOfBlogPost = isAuthorOfBlogPost
    setViewState(update)
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.setQueryExhausted(isExhausted: Boolean){
    val update = getCurrentViewStateOrNew()
    update.blogFields.isQueryExhausted = isExhausted
    setViewState(update)
}


// Filter can be "date_updated" or "username"
@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.setBlogFilter(filter: String?){
    filter?.let{
        val update = getCurrentViewStateOrNew()
        update.blogFields.filter = filter
        setViewState(update)
    }
}

// Order can be "-" or ""
// Note: "-" = DESC, "" = ASC
@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.setBlogOrder(order: String){
    val update = getCurrentViewStateOrNew()
    update.blogFields.order = order
    setViewState(update)
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.setLayoutManagerState(layoutManagerState: Parcelable){
    val update = getCurrentViewStateOrNew()
    update.blogFields.layoutManagerState = layoutManagerState
    setViewState(update)
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.clearLayoutManagerState(){
    val update = getCurrentViewStateOrNew()
    update.blogFields.layoutManagerState = null
    setViewState(update)
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.removeDeletedBlogPost(){
    val update = getCurrentViewStateOrNew()
    val list = update.blogFields.blogList?.toMutableList()
    if(list != null){
        for(i in 0..(list.size - 1)){
            if(list[i] == getBlogPost()){
                list.remove(getBlogPost())
                break
            }
        }
        setBlogListData(list)
    }
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.updateListItem(newBlogPost: BlogPost){
    val update = getCurrentViewStateOrNew()
    val list = update.blogFields.blogList?.toMutableList()
    if(list != null){
        for(i in 0..(list.size - 1)){
            if(list[i].pk == newBlogPost.pk){
                list[i] = newBlogPost
                break
            }
        }
        update.blogFields.blogList = list
        setViewState(update)
    }
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.onBlogPostUpdateSuccess(blogPost: BlogPost){
    setBlogPost(blogPost) // update ViewBlogFragment
    updateListItem(blogPost) // update BlogFragment
}


@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.setUpdatedUri(uri: Uri){
    val update = getCurrentViewStateOrNew()
    val updatedBlogFields = update.updatedBlogFields
    updatedBlogFields.updatedImageUri = uri
    update.updatedBlogFields = updatedBlogFields
    setViewState(update)
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.setUpdatedTitle(title: String){
    val update = getCurrentViewStateOrNew()
    val updatedBlogFields = update.updatedBlogFields
    updatedBlogFields.updatedBlogTitle = title
    update.updatedBlogFields = updatedBlogFields
    setViewState(update)
}


@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun BlogViewModel.setUpdatedBody(body: String){
    val update = getCurrentViewStateOrNew()
    val updatedBlogFields = update.updatedBlogFields
    updatedBlogFields.updatedBlogBody = body
    update.updatedBlogFields = updatedBlogFields
    setViewState(update)
}













