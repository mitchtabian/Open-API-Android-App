package com.templateapp.cloudapi.business.interactors.task

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.datasource.cache.task.toTask
import com.templateapp.cloudapi.business.domain.util.DataState
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_TASK_UNABLE_TO_RETRIEVE
import com.templateapp.cloudapi.business.domain.util.MessageType
import com.templateapp.cloudapi.business.domain.util.Response
import com.templateapp.cloudapi.business.domain.util.UIComponentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetTaskFromCache(
    private val cache: TaskDao,
) {

    fun execute(
        id: String,
    ): Flow<DataState<Task>> = flow{
        emit(DataState.loading<Task>())
        val task = cache.getTask(id)?.toTask()

        if(task != null){
            emit(DataState.data(response = null, data = task))
        }
        else{
            emit(DataState.error<Task>(
                response = Response(
                    message = ERROR_TASK_UNABLE_TO_RETRIEVE,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            ))
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}



















