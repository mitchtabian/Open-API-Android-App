package com.templateapp.cloudapi.business.interactors.task

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.GENERIC_ERROR
import com.templateapp.cloudapi.business.domain.util.SuccessHandling.Companion.SUCCESS_TASK_UPDATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UpdateTask(
    private val service: OpenApiMainService,
    private val cache: TaskDao,
) {

    /**
     * If successful this will emit a string saying: 'updated'
     */
    fun execute(
        authToken: AuthToken?,
        id: String,
        completed: Boolean,
        title: RequestBody,
        description: RequestBody,
        image: MultipartBody.Part?,
    ): Flow<DataState<Response>> = flow{
        emit(DataState.loading<Response>())
        if(authToken == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        // attempt update
        val createUpdateResponse = service.updateTask(
            authToken.token,
            id = id,
            completed = completed,
            title = title,
            description = description,
            image = image
        )

        if(createUpdateResponse.response == GENERIC_ERROR){ // failure
            throw Exception(createUpdateResponse.error)
        }else{ // success
            cache.updateTask(
                id = createUpdateResponse.task._id,
                completed = createUpdateResponse.task.completed,
                title = createUpdateResponse.task.title,
                description = createUpdateResponse.task.description,
                updatedAt = DateUtils.convertServerStringDateToLong(createUpdateResponse.task.updatedAt),
                createdAt = DateUtils.convertServerStringDateToLong(createUpdateResponse.task.createdAt),
                image = createUpdateResponse.task.image
            )
            // Tell the UI it was successful
            emit(DataState.data<Response>(
                data = Response(
                    message = SUCCESS_TASK_UPDATED,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Success()
                ),
                response = null,
            ))
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}









