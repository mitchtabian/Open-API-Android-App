package com.codingwithmitch.openapi.presentation.main.blog.list

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.openapi.business.datasource.datastore.DataStoreManager
import com.codingwithmitch.openapi.business.domain.util.StateMessage
import com.codingwithmitch.openapi.business.domain.util.doesMessageAlreadyExistInQueue
import com.codingwithmitch.openapi.business.interactors.blog.GetOrderAndFilter
import com.codingwithmitch.openapi.business.interactors.blog.SearchBlogs
import com.codingwithmitch.openapi.presentation.session.SessionManager
import com.codingwithmitch.openapi.presentation.util.DataStoreKeys.Companion.BLOG_FILTER
import com.codingwithmitch.openapi.presentation.util.DataStoreKeys.Companion.BLOG_ORDER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class BlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val searchBlogs: SearchBlogs,
    private val getOrderAndFilter: GetOrderAndFilter,
    private val dataStoreManager: DataStoreManager,
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<BlogState> = MutableLiveData(BlogState())

    init {
        onTriggerEvent(BlogEvents.GetOrderAndFilter)
//        viewModelScope.launch {
//            val currentFilter = dataStoreManager.readValue(BLOG_FILTER)?.let { filter ->
//                getFilterFromValue(filter)
//            }?: getFilterFromValue(BlogFilterOptions.DATE_UPDATED.value)
//            state.value = state.value?.copy(filter = currentFilter)
//            val currentOrder = dataStoreManager.readValue(BLOG_ORDER)?.let { order ->
//                getOrderFromValue(order)
//            }?: getOrderFromValue(BlogOrderOptions.DESC.value)
//            state.value = state.value?.copy(order = currentOrder)
//            onTriggerEvent(BlogEvents.NewSearch)
//        }
    }

    fun onTriggerEvent(event: BlogEvents) {
        when (event) {
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
            is BlogEvents.GetOrderAndFilter -> {
                getOrderAndFilter()
            }
            is BlogEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is BlogEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun getOrderAndFilter() {
        state.value?.let { state ->
            getOrderAndFilter.execute().onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { orderAndFilter ->
                    val order = orderAndFilter.order
                    val filter = orderAndFilter.filter
                    this.state.value = state.copy(
                        order = order,
                        filter = filter
                    )
                    onTriggerEvent(BlogEvents.NewSearch)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }


    private fun saveFilterOptions(filter: String, order: String) {
        viewModelScope.launch {
            dataStoreManager.setValue(BLOG_FILTER, filter)
            dataStoreManager.setValue(BLOG_ORDER, order)
        }
    }

    private fun removeHeadFromQueue() {
        state.value?.let { state ->
            try {
                val queue = state.queue
                queue.remove() // can throw exception if empty
                this.state.value = state.copy(queue = queue)
            } catch (e: Exception) {
                Log.d(TAG, "removeHeadFromQueue: Nothing to remove from DialogQueue")
            }
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage) {
        state.value?.let { state ->
            val queue = state.queue
            if (!stateMessage.doesMessageAlreadyExistInQueue(queue = queue)) {
                queue.add(stateMessage)
                this.state.value = state.copy(queue = queue)
            }
        }
    }

    private fun clearList() {
        state.value?.let { state ->
            this.state.value = state.copy(blogList = listOf())
        }
    }

    private fun resetPage() {
        state.value = state.value?.copy(page = 1)
    }

    private fun incrementPageNumber() {
        state.value?.let { state ->
            this.state.value = state.copy(page = state.page + 1)
        }
    }

    private fun onUpdateQuery(query: String) {
        state.value = state.value?.copy(query = query)
    }

    private fun onUpdateFilter(filter: BlogFilterOptions) {
        state.value?.let { state ->
            this.state.value = state.copy(filter = filter)
            saveFilterOptions(filter.value, state.order.value)
        }
    }

    private fun onUpdateOrder(order: BlogOrderOptions) {
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

    private fun nextPage() {
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
















