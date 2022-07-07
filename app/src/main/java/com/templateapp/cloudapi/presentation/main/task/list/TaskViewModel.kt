package com.templateapp.cloudapi.presentation.main.task.list

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.templateapp.cloudapi.business.datasource.datastore.AppDataStore
import com.templateapp.cloudapi.business.domain.util.ErrorHandling
import com.templateapp.cloudapi.business.domain.util.StateMessage
import com.templateapp.cloudapi.business.domain.util.UIComponentType
import com.templateapp.cloudapi.business.domain.util.doesMessageAlreadyExistInQueue
import com.templateapp.cloudapi.business.interactors.task.GetOrderAndFilter
import com.templateapp.cloudapi.business.interactors.task.SearchTasks
import com.templateapp.cloudapi.presentation.session.SessionManager
import com.templateapp.cloudapi.presentation.util.DataStoreKeys.Companion.TASK_FILTER
import com.templateapp.cloudapi.presentation.util.DataStoreKeys.Companion.TASK_ORDER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    private val searchTasks: SearchTasks,
    private val getOrderAndFilter: GetOrderAndFilter,
    private val appDataStoreManager: AppDataStore,
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<TaskState> = MutableLiveData(TaskState())

    init {
        onTriggerEvent(TaskEvents.GetOrderAndFilter)
    }

    fun onTriggerEvent(event: TaskEvents) {
        when (event) {
            is TaskEvents.NewSearch -> {
                search()
            }
            is TaskEvents.NextPage -> {
                nextPage()
            }
            is TaskEvents.UpdateFilter -> {
                onUpdateFilter(event.filter)
            }
            is TaskEvents.UpdateQuery -> {
                onUpdateQuery(event.query)
            }
            is TaskEvents.UpdateOrder -> {
                onUpdateOrder(event.order)
            }
            is TaskEvents.GetOrderAndFilter -> {
                getOrderAndFilter()
            }
            is TaskEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is TaskEvents.OnRemoveHeadFromQueue -> {
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
                    onTriggerEvent(TaskEvents.NewSearch)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }


    private fun saveFilterOptions(filter: String, order: String) {
        viewModelScope.launch {
            appDataStoreManager.setValue(TASK_FILTER, filter)
            appDataStoreManager.setValue(TASK_ORDER, order)
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

    private fun appendToMessageQueue(stateMessage: StateMessage){
        state.value?.let { state ->
            val queue = state.queue
            if(!stateMessage.doesMessageAlreadyExistInQueue(queue = queue)){
                if(!(stateMessage.response.uiComponentType is UIComponentType.None)){
                    queue.add(stateMessage)
                    this.state.value = state.copy(queue = queue)
                }
            }
        }
    }

    private fun onUpdateQueryExhausted(isExhausted: Boolean) {
        state.value?.let { state ->
            this.state.value = state.copy(isQueryExhausted = isExhausted)
        }
    }

    private fun clearList() {
        state.value?.let { state ->
            this.state.value = state.copy(tasksList = listOf())
        }
    }

    private fun resetPage() {
        state.value = state.value?.copy(page = 1)
        onUpdateQueryExhausted(false)
    }

    private fun incrementPageNumber() {
        state.value?.let { state ->
            this.state.value = state.copy(page = state.page + 1)
        }
    }

    private fun onUpdateQuery(query: String) {
        state.value = state.value?.copy(query = query)
    }

    private fun onUpdateFilter(filter: TaskFilterOptions) {
        state.value?.let { state ->
            this.state.value = state.copy(filter = filter)
            saveFilterOptions(filter.value, state.order.value)
        }
    }

    private fun onUpdateOrder(order: TaskOrderOptions) {
        state.value?.let { state ->
            this.state.value = state.copy(order = order)
            saveFilterOptions(state.filter.value, order.value)
        }
    }

    private fun search() {
        resetPage()
        clearList()
        state.value?.let { state ->
            searchTasks.execute(
                authToken = sessionManager.state.value?.authToken,
                query = state.query,
                page = state.page,
                filter = state.filter,
                order = state.order
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    this.state.value = state.copy(tasksList = list)
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){
                        onUpdateQueryExhausted(true)
                    }else{
                        appendToMessageQueue(stateMessage)
                    }
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun nextPage() {
        incrementPageNumber()
        state.value?.let { state ->
            searchTasks.execute(
                authToken = sessionManager.state.value?.authToken,
                query = state.query,
                page = state.page,
                filter = state.filter,
                order = state.order
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    this.state.value = state.copy(tasksList = list)
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){
                        onUpdateQueryExhausted(true)
                    }else{
                        appendToMessageQueue(stateMessage)
                    }
                }

            }.launchIn(viewModelScope)
        }
    }

}
















