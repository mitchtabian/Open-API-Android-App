package com.codingwithmitch.openapi.ui.main.blog.list

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.openapi.interactors.blog.SearchBlogs
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.util.PreferenceKeys.Companion.BLOG_FILTER
import com.codingwithmitch.openapi.util.PreferenceKeys.Companion.BLOG_ORDER
import com.codingwithmitch.openapi.util.StateMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class BlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val searchBlogs: SearchBlogs,
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
): ViewModel(){

    val state: MutableLiveData<BlogState> = MutableLiveData(BlogState())

    init {
        val orderValue = sharedPreferences.getString(
            BLOG_ORDER,
            BlogOrderOptions.DESC.value
        )
        onUpdateOrder(getOrderFromValue(orderValue))
        val filterValue = sharedPreferences.getString(
            BLOG_FILTER,
            BlogFilterOptions.DATE_UPDATED.value
        )
        onUpdateFilter(getFilterFromValue(filterValue))
        onTriggerEvent(BlogEvents.NewSearch)
    }

    fun onTriggerEvent(event: BlogEvents) {
        when(event){
            is BlogEvents.NewSearch -> {
                search()
            }
            is BlogEvents.NextPage -> {
                nextPage()
            }
            is BlogEvents.UpdateFilter -> {
                onUpdateFilter(event.filter)
            }
            is BlogEvents.UpdateQuery -> {
                onUpdateQuery(event.query)
            }
            is BlogEvents.UpdateOrder -> {
                onUpdateOrder(event.order)
            }
            is BlogEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
        }
    }


    private fun saveFilterOptions(filter: String, order: String){
        editor.putString(BLOG_FILTER, filter)
        editor.apply()

        editor.putString(BLOG_ORDER, order)
        editor.apply()
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        // TODO
    }

    private fun appendBlogPosts(blogs: List<BlogPost>){
        state.value?.let { state ->
            val curr: MutableList<BlogPost> = mutableListOf()
            curr.addAll(state.blogList)
            curr.addAll(blogs)
            this.state.value = state.copy(blogList = curr)
        }
    }

    private fun clearList(){
        state.value?.let { state ->
            this.state.value = state.copy(blogList = listOf())
        }
    }

    private fun resetPage(){
        state.value = state.value?.copy(page = 1)
    }

    private fun incrementPageNumber(){
        state.value?.let { state ->
            this.state.value = state.copy(page = state.page + 1)
        }
    }

    private fun onUpdateQuery(query: String){
        state.value = state.value?.copy(query = query)
    }

    private fun onUpdateFilter(filter: BlogFilterOptions){
        state.value?.let { state ->
            this.state.value = state.copy(filter = filter)
            saveFilterOptions(filter.value, state.order.value)
        }
    }

    private fun onUpdateOrder(order: BlogOrderOptions){
        state.value?.let { state ->
            this.state.value = state.copy(order = order)
            saveFilterOptions(state.filter.value, order.value)
        }
    }

    private fun search() {
        state.value?.let { state ->
            resetPage()
            clearList()
            searchBlogs.execute(
                authToken = sessionManager.state.value?.authToken,
                query = state.query,
                page = state.page,
                filter = state.filter,
                order = state.order
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    this.state.value = state.copy(blogList = list)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun nextPage(){
        state.value?.let { state ->
            incrementPageNumber()
            resetPage()
            searchBlogs.execute(
                authToken = sessionManager.state.value?.authToken,
                query = state.query,
                page = state.page,
                filter = state.filter,
                order = state.order
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    appendBlogPosts(list)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

}
















