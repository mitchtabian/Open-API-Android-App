package com.templateapp.cloudapi.business.interactors.task

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.datastore.AppDataStore
import com.templateapp.cloudapi.business.domain.util.DataState
import com.templateapp.cloudapi.presentation.main.task.list.*
import com.templateapp.cloudapi.presentation.util.DataStoreKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetOrderAndFilter(
    private val appDataStoreManager: AppDataStore
) {
    fun execute(): Flow<DataState<OrderAndFilter>> = flow {
        emit(DataState.loading<OrderAndFilter>())
        val filter = appDataStoreManager.readValue(DataStoreKeys.TASK_FILTER)?.let { filter ->
            getFilterFromValue(filter)
        }?: getFilterFromValue(TaskFilterOptions.DATE_CREATED.value)
        val order = appDataStoreManager.readValue(DataStoreKeys.TASK_ORDER)?.let { order ->
            getOrderFromValue(order)
        }?: getOrderFromValue(TaskOrderOptions.ASC.value)
        emit(DataState.data(
            response = null,
            data = OrderAndFilter(order = order, filter = filter)
        ))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}










