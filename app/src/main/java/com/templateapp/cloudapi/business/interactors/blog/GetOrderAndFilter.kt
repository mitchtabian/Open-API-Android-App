package com.templateapp.cloudapi.business.interactors.blog

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.datastore.AppDataStore
import com.templateapp.cloudapi.business.domain.util.DataState
import com.templateapp.cloudapi.presentation.main.blog.list.*
import com.templateapp.cloudapi.presentation.util.DataStoreKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetOrderAndFilter(
    private val appDataStoreManager: AppDataStore
) {
    fun execute(): Flow<DataState<OrderAndFilter>> = flow {
        emit(DataState.loading<OrderAndFilter>())
        val filter = appDataStoreManager.readValue(DataStoreKeys.BLOG_FILTER)?.let { filter ->
            getFilterFromValue(filter)
        }?: getFilterFromValue(BlogFilterOptions.DATE_UPDATED.value)
        val order = appDataStoreManager.readValue(DataStoreKeys.BLOG_ORDER)?.let { order ->
            getOrderFromValue(order)
        }?: getOrderFromValue(BlogOrderOptions.DESC.value)
        emit(DataState.data(
            response = null,
            data = OrderAndFilter(order = order, filter = filter)
        ))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}










