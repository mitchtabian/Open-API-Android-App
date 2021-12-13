package com.templateapp.cloudapi.business.interactors.task

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.datasource.network.main.toTask
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.datasource.cache.task.*
import com.templateapp.cloudapi.business.domain.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.templateapp.cloudapi.presentation.main.task.list.TaskFilterOptions
import com.templateapp.cloudapi.presentation.main.task.list.TaskOrderOptions
import com.templateapp.cloudapi.business.domain.util.DataState
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import com.templateapp.cloudapi.business.domain.util.MessageType
import com.templateapp.cloudapi.business.domain.util.Response
import com.templateapp.cloudapi.business.domain.util.UIComponentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SearchTasks(
    private val service: OpenApiMainService,
    private val cache: TaskDao,
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        query: String,
        page: Int,
        filter: TaskFilterOptions,
        order: TaskOrderOptions,
    ): Flow<DataState<List<Task>>> = flow {
        emit(DataState.loading<List<Task>>())
        if(authToken == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        // get Tasks from network
        val filterAndOrder = filter.value + order.value // Ex: modifiedAt:desc

        try{ // catch network exception
            val tasks = service.searchListTasks(
                "${authToken.token}",
                query = query,
                sortBy = filterAndOrder,
                skip = (page - 1) * PAGINATION_PAGE_SIZE,
                limit = PAGINATION_PAGE_SIZE
            ).results.map { it.toTask() }

            // Insert into cache
            for(task in tasks){
                try{
                    cache.insert(task.toEntity())
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }catch (e: Exception){
            emit(
                DataState.error<List<Task>>(
                    response = Response(
                        message = "Unable to update the cache.",
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }

        // emit from cache
        val cachedTasks = cache.returnOrderedTaskQuery(
            query = query,
            filterAndOrder = filterAndOrder,
            page = page
        ).map { it.toTask() }

        emit(DataState.data(response = null, data = cachedTasks))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}



















