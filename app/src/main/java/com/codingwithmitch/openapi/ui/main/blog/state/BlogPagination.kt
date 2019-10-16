package com.codingwithmitch.openapi.ui.main.blog.state


import android.util.Log


fun BlogViewModel.resetPage(){
    val update = getCurrentViewStateOrNew()
    update.blogFields.page = 1
    setViewState(update)
}

fun BlogViewModel.incrementPageNumber(){
    val update = getCurrentViewStateOrNew()
    val page = update.copy().blogFields.page
    update.blogFields.page = page + 1
}

fun BlogViewModel.handleIncomingBlogListData(viewState: BlogViewState){
    Log.d(TAG, "BlogViewModel, DataState: ${viewState}")
    Log.d(TAG, "BlogViewModel, DataState: isQueryInProgress?: " +
            "${viewState.blogFields.isQueryInProgress}")
    Log.d(TAG, "BlogViewModel, DataState: isQueryExhausted?: " +
            "${viewState.blogFields.isQueryExhausted}")
    setQueryInProgress(viewState.blogFields.isQueryInProgress)
    setQueryExhausted(viewState.blogFields.isQueryExhausted)
    setBlogListData(viewState.blogFields.blogList)
}



