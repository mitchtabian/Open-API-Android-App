package com.templateapp.cloudapi.business.interactors.task

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.datasource.network.main.responses.toTask
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.datasource.cache.task.toEntity
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception

class PublishTask(
    private val service: OpenApiMainService,
    private val cache: TaskDao,
    private val serverMsgTranslator: ServerMsgTranslator
){
    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        title: RequestBody,
        body: RequestBody,
        completed: Boolean,
        image: MultipartBody.Part?,
    ): Flow<DataState<Response>> = flow {
        emit(DataState.loading<Response>())
        if(authToken == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        // attempt update
        val createResponse = service.createTask(
            authToken.token,
            title = title,
            description = body,
            completed = completed,
            image= image
        )

        // If they don't have a paid membership account it will still return a 200 with failure message
        // Need to account for that

        if(createResponse.response == ErrorHandling.GENERIC_ERROR){
            throw Exception(createResponse.error)
        }

        // insert the new task into the cache
        cache.insert(createResponse.toTask().toEntity())

        // Tell the UI it was successful
        emit(DataState.data<Response>(
            data = Response(
                message = SuccessHandling.SUCCESS_TASK_CREATED,
                uiComponentType = UIComponentType.None(),
                messageType = MessageType.Success()
            ),
            response = null,
        ))
    }.catch { e ->
        emit(handleUseCaseException(e, serverMsgTranslator))
    }
}






















