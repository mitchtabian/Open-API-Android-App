package com.codingwithmitch.openapi.ui.main.blog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.openapi.repository.main.BlogRepository
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.main.blog.state.BlogDataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class BlogViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val blogRepository: BlogRepository
)
    : ViewModel()
{

    private val TAG: String = "AppDebug"

    private val dataState: MediatorLiveData<BlogDataState> = MediatorLiveData()

    fun observeDataState(): LiveData<BlogDataState> {
        return dataState
    }

    fun searchBlogPosts(query: String, ordering: String, page: Int) {
        sessionManager.observeSession().value?.authToken?.let { authToken ->
            val source = blogRepository.searchBlogPosts(authToken, query, ordering, page)
            dataState.addSource(source) {
                it.error?.let {
                    dataState.removeSource(source)
                }
                it.success?.let {
                    dataState.removeSource(source)
                }
                setDataState(it)
            }
        }
    }

    fun setDataState(
        newDataState: BlogDataState? = null
    ){
        viewModelScope.launch(Dispatchers.Main) {

            if(newDataState == null){
                dataState.value = BlogDataState()
            }
            if(dataState.value == null){
                dataState.value = BlogDataState()
            }

            // LOADING
            newDataState?.loading?.let {loading ->
                dataState.value?.let {
                    it.loading = loading
                    dataState.value = it
                }
            }

            // BLOG_LIST
            newDataState?.blogPostList?.let {blogList ->
                dataState.value?.let {
                    it.blogPostList = blogList
                    dataState.value = it
                }
            }

            // ERROR
            newDataState?.error?.let {newStateError ->
                dataState.value?.let {
                    it.error = newStateError
                    it.loading = null
                    dataState.value = it
                }
                clearStateMessages()
            }

            // SUCCESS
            newDataState?.success?.let {successResponse ->
                dataState.value?.let {
                    it.loading = null
                    it.success = successResponse
                    dataState.value = it
                }
                clearStateMessages()
            }


        }
    }


    /**
     * Clear SuccessResponse and Error from State.
     * That was if back button is pressed we don't get duplicates
     */
    fun clearStateMessages(){
        dataState.value?.let {
            it.success = null
            it.error = null
            dataState.value = it
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        blogRepository.cancelRequests()
    }
}


















