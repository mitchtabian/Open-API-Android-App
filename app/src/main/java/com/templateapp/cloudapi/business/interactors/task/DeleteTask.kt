package com.templateapp.cloudapi.business.interactors.task

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.datasource.cache.task.toEntity
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_DELETE_TASK_DOES_NOT_EXIST
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.GENERIC_ERROR
import com.templateapp.cloudapi.business.domain.util.SuccessHandling.Companion.SUCCESS_TASK_DELETED
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class DeleteTask(
    private val service: OpenApiMainService,
    private val cache: TaskDao,
    private val serverMsgTranslator: ServerMsgTranslator
) {
    /**
     * If successful this will emit a string saying: 'deleted'
     */
    fun execute(
        authToken: AuthToken?,
        task: Task,
    ): Flow<DataState<Response>> = flow{
        emit(DataState.loading<Response>())
        if(authToken == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        // attempt delete from network
        val response = service.deleteTask(
            "${authToken.token}",
            task.id
        )
        if(response.response == GENERIC_ERROR
            && response.errorMessage != ERROR_DELETE_TASK_DOES_NOT_EXIST){ // failure
            throw Exception(response.errorMessage)
        }else{
            // delete from cache
            cache.deleteTask(task.toEntity())
            // Tell the UI it was successful
            emit(DataState.data<Response>(
                data = Response(
                    message = SUCCESS_TASK_DELETED,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Success()
                ),
                response = null
            ))
        }
    }.catch { e ->
        emit(handleUseCaseException(e, serverMsgTranslator))
    }
}
















