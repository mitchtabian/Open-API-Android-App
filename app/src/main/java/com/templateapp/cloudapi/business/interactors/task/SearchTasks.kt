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
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_TASK_DOES_NOT_EXIST
import com.templateapp.cloudapi.business.domain.util.MessageType
import com.templateapp.cloudapi.business.domain.util.Response
import com.templateapp.cloudapi.business.domain.util.UIComponentType
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SearchTasks(
    private val service: OpenApiMainService,
    private val cache: TaskDao,
    private val serverMsgTranslator: ServerMsgTranslator
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

        // load and check if tasks that are in cache are indeed present on the server
        var keepSearching = true;
        while(keepSearching){
            val cachedTasks = cache.returnOrderedTaskQuery(
                query = query,
                filterAndOrder = filterAndOrder,
                page = page
            )
            val cachedTaskSize = cachedTasks.size

            for(cachedTask in cachedTasks){
                try { // try to load each task and check if it exists on the server
                    val serverTask = service.getTask(
                        "${authToken.token}",
                        id = cachedTask.id
                    )
                    // If task was not found on server, delete task from cache.
                    if(serverTask?.error?.contains(ERROR_TASK_DOES_NOT_EXIST) == true) {
                        cache.deleteTask(cachedTask.id)
                    }
                }catch (e: Exception){
                    emit(
                        DataState.error<List<Task>>(
                            response = Response(
                                message = "Unable to get the task from the server. Bad connection?",
                                uiComponentType = UIComponentType.None(),
                                messageType = MessageType.Error()
                            )
                        )
                    )
                }
            }
            // Stop searching once no tasks were deleted from the cache, as they all appear to be also on the server.
            if(cachedTaskSize == cachedTasks.size)
                keepSearching = false;
        }

        // Return cache to the caller
        val cachedTasks = cache.returnOrderedTaskQuery(
            query = query,
            filterAndOrder = filterAndOrder,
            page = page
        ).map { it.toTask() }

        emit(DataState.data(response = null, data = cachedTasks))
    }.catch { e ->
        emit(handleUseCaseException(e, serverMsgTranslator))
    }
}



















