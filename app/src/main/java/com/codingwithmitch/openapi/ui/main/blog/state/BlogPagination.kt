package com.codingwithmitch.openapi.ui.main.blog.state

import android.util.Log
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.main.blog.BlogFragment
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent.*
import com.codingwithmitch.openapi.util.ErrorHandling


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

fun BlogViewModel.loadFirstPage() {
    setQueryInProgress(true)
    setQueryExhausted(false)
    resetPage()
    setStateEvent(BlogSearchEvent())
    Log.e(TAG, "BlogViewModel: loadFirstPage: ${viewState.value!!.blogFields.searchQuery}")
}

fun BlogFragment.handlePagination(dataState: DataState<BlogViewState>){

    // Handle incoming data from DataState
    dataState.data?.let {
        it.data?.let{
            it.getContentIfNotHandled()?.let{
                Log.d(TAG, "BlogFragment, DataState: ${it}")
                Log.d(TAG, "BlogFragment, DataState: isQueryInProgress?: ${it.blogFields.isQueryInProgress}")
                Log.d(TAG, "BlogFragment, DataState: isQueryExhausted?: ${it.blogFields.isQueryExhausted}")
                viewModel.setQueryInProgress(it.blogFields.isQueryInProgress)
                viewModel.setQueryExhausted(it.blogFields.isQueryExhausted)
                viewModel.setBlogListData(it.blogFields.blogList)
            }
        }
    }

    // Check for pagination end (no more results)
    // must do this b/c server will return an ApiErrorResponse if page is not valid,
    // -> meaning there is no more data.
    dataState.error?.let{ event ->
        event.peekContent().response.message?.let{
            if(ErrorHandling.isPaginationDone(it)){

                // handle the error message event so it doesn't display in UI
                event.getContentIfNotHandled()

                // set query exhausted to update RecyclerView with "No more results..." list item
                viewModel.setQueryExhausted(true)
            }
        }
    }

}





