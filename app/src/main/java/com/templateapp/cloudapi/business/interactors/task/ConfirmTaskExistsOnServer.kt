package com.templateapp.cloudapi.business.interactors.task

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_TASK_DOES_NOT_EXIST
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.UNAUTHORIZED_ERROR
import com.templateapp.cloudapi.business.domain.util.SuccessHandling.Companion.SUCCESS_TASK_DOES_NOT_EXIST_IN_CACHE
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

/**
 * If a task exists in the cache but does not exist on server, we need to delete from cache.
 */
class ConfirmTaskExistsOnServer(
    private val service: OpenApiMainService,
    private val cache: TaskDao,
    private val serverMsgTranslator: ServerMsgTranslator
) {

    fun execute(
        authToken: AuthToken?,
        id: String
    ): Flow<DataState<Response>> =  flow {
        emit(DataState.loading<Response>())
        val cachedTask = cache.getTask(id)
        if(cachedTask == null){
            // It doesn't exist in cache. Finish.
            emit(DataState.data<Response>(
                data = Response(
                    message = SUCCESS_TASK_DOES_NOT_EXIST_IN_CACHE,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Success()
                ),
                response = null,
            ))
        }else{
            if(authToken == null){
                throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
            }
            // confirm it exists on server (throws 404 if does not exist)
            var isNetworkError = false
            // check for auth error (throws 401 if token error)
            var isAuthorizationError = false
            val task = try {
                service.getTask(
                    authorization = "${authToken.token}",
                    id = id,
                )
            }catch (e1: Exception){
                if(e1.message?.contains(UNABLE_TO_RESOLVE_HOST) == true){ // network error
                    isNetworkError = true
                }
                if(e1.message?.contains(UNAUTHORIZED_ERROR) == true){ // authorization error
                    isAuthorizationError = true
                }
                e1.printStackTrace()
                null
            }
            if(isNetworkError){
                emit(
                    DataState.error<Response>(
                        response = Response(
                            message = "Network Error.",
                            uiComponentType = UIComponentType.None(),
                            messageType = MessageType.Error()
                        )
                    )
                )
            }else{
                if(isAuthorizationError){
                    emit(
                        DataState.error<Response>(
                            response = Response(
                                message = "Authorization error. Please restart the app.",
                                uiComponentType = UIComponentType.Dialog(),
                                messageType = MessageType.Error()
                            )
                        )
                    )
                }else{
                    // if it exists on server but not in cache. Delete from cache and emit error.
                    if (task?.error?.contains(ERROR_TASK_DOES_NOT_EXIST) == true) {
                        cache.deleteTask(id)
                        emit(
                            DataState.error<Response>(
                                response = Response(
                                    message = ERROR_TASK_DOES_NOT_EXIST,
                                    uiComponentType = UIComponentType.Dialog(),
                                    messageType = MessageType.Error()
                                )
                            )
                        )

                    } else { // if it exists in the cache and on the server. Everything is fine.
                        emit(
                            DataState.data<Response>(
                                data = Response(
                                    message = SuccessHandling.SUCCESS_TASK_EXISTS_ON_SERVER,
                                    uiComponentType = UIComponentType.None(),
                                    messageType = MessageType.Success()
                                ),
                                response = null,
                            )
                        )
                    }
                }
            }
        }
    }.catch { e ->
        emit(handleUseCaseException(e, serverMsgTranslator))
    }
}
















