package com.templateapp.cloudapi.business.interactors.account

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.cache.account.*
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.datasource.network.main.toAccount
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.datasource.cache.auth.AuthTokenDao
import com.templateapp.cloudapi.business.datasource.cache.auth.toEntity
import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.datasource.cache.task.returnOrderedTaskQuery
import com.templateapp.cloudapi.business.datasource.cache.task.toEntity
import com.templateapp.cloudapi.business.datasource.cache.task.toTask
import com.templateapp.cloudapi.business.datasource.network.main.responses.toList
import com.templateapp.cloudapi.business.datasource.network.main.responses.toRole
import com.templateapp.cloudapi.business.datasource.network.main.toTask
import com.templateapp.cloudapi.business.domain.models.Role
import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_UNABLE_TO_RETRIEVE_ACCOUNT_DETAILS
import com.templateapp.cloudapi.presentation.main.task.list.TaskFilterOptions
import com.templateapp.cloudapi.presentation.main.task.list.TaskOrderOptions
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class GetAllRoles(
    private val service: OpenApiMainService,
    private val cache: RoleDao,
    private val serverMsgTranslator: ServerMsgTranslator
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
    ): Flow<DataState<List<Role>>> = flow {
        emit(DataState.loading<List<Role>>())
        if (authToken == null) {
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        // get Tasks from network
        try { // catch network exception
            val roles = service.getAllRoles(
                "${authToken.token}",
            ).roles.map { it.toRole() }

            // Insert into cache
           for (role in roles) {
                try {
                    cache.insert(role.toEntity())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            val cachedUsers = cache.getAllRoles().map { it.toRole() }


            emit(DataState.data(response = null, data = cachedUsers))

        } catch (e: Exception) {
            System.out.println("dkjhdkj" + e)
            emit(
                DataState.error<List<Role>>(
                    response = Response(
                        message = "Unable get all roles.",
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
            // load and check if tasks that are in cache are indeed present on the server


        }
    }.catch { e ->
        emit(handleUseCaseException(e, serverMsgTranslator))
    }
}


