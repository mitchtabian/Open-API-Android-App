package com.codingwithmitch.openapi.ui.main.blog

import androidx.lifecycle.*
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.repository.main.BlogRepository
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.BaseViewModel
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent.*
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState
import com.codingwithmitch.openapi.util.AbsentLiveData
import javax.inject.Inject

class BlogViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val blogRepository: BlogRepository
)
    : BaseViewModel<BlogStateEvent, BlogViewState>()
{

    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        when(stateEvent){
            is BlogSearchEvent -> {
                return sessionManager.observeSession().value?.authToken?.let { authToken ->
                    blogRepository.searchBlogPosts(
                        authToken,
                        stateEvent.searchQuery,
                        stateEvent.order, // can't be null. see @BlogViewState class
                        stateEvent.page // can't be null. see @BlogViewState class
                    )
                }?: AbsentLiveData.create()
            }

            // is NextPageEvent
            //  -> When scrolling down

            // is ChangeOrderEvent
            //  -> New filter criteria is selected

            // is BlogSelectedEvent
            //  -> Select a blog post from the list. Navigate to ViewBlogFragment

            //
        }
    }

    fun setBlogListData(blogList: List<BlogPost>){
        if(_viewState.value?.blogList == blogList){
            return
        }
        val update = _viewState.value?.let{
            it
        }?: BlogViewState()
        update.blogList = blogList
        _viewState.value = update
    }

    fun cancelRequests(){
        blogRepository.cancelRequests()
    }

    override fun onCleared() {
        super.onCleared()
        cancelRequests()
    }
}


















