package com.codingwithmitch.openapi.presentation.main.blog.list

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.openapi.business.domain.util.StateMessage
import com.codingwithmitch.openapi.business.domain.util.doesMessageAlreadyExistInQueue
import com.codingwithmitch.openapi.business.interactors.blog.SearchBlogs
import com.codingwithmitch.openapi.presentation.session.SessionManager
import com.codingwithmitch.openapi.presentation.util.PreferenceKeys.Companion.BLOG_FILTER
import com.codingwithmitch.openapi.presentation.util.PreferenceKeys.Companion.BLOG_ORDER
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

    private val TAG: String = "AppDebug"

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
            is BlogEvents.OnRemoveHeadFromQueue ->{
                removeHeadFromQueue()
            }
        }
    }


    private fun saveFilterOptions(filter: String, order: String){
        editor.putString(BLOG_FILTER, filter)
        editor.apply()

        editor.putString(BLOG_ORDER, order)
        editor.apply()
    }

    private fun removeHeadFromQueue(){
        state.value?.let { state ->
            try {
                val queue = state.queue
                queue.remove() // can throw exception if empty
                this.state.value = state.copy(queue = queue)
            }catch (e: Exception){
                Log.d(TAG, "removeHeadFromQueue: Nothing to remove from DialogQueue")
            }
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        state.value?.let { state ->
            val queue = state.queue
            if(!stateMessage.doesMessageAlreadyExistInQueue(queue = queue)){
                queue.add(stateMessage)
                this.state.value = state.copy(queue = queue)
            }
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
        resetPage()
        clearList()
        state.value?.let { state ->
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
        incrementPageNumber()
        state.value?.let { state ->
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

}















