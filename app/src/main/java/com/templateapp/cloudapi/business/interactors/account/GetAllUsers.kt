package com.templateapp.cloudapi.business.interactors.account

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.datasource.network.main.toAccount
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.datasource.cache.account.AccountDao
import com.templateapp.cloudapi.business.datasource.cache.account.toAccount
import com.templateapp.cloudapi.business.datasource.cache.account.toEntity
import com.templateapp.cloudapi.business.datasource.cache.auth.AuthTokenDao
import com.templateapp.cloudapi.business.datasource.cache.auth.toEntity
import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.datasource.cache.task.returnOrderedTaskQuery
import com.templateapp.cloudapi.business.datasource.cache.task.toEntity
import com.templateapp.cloudapi.business.datasource.cache.task.toTask
import com.templateapp.cloudapi.business.datasource.network.main.responses.toList
import com.templateapp.cloudapi.business.datasource.network.main.toTask
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

class GetAllUsers(
    private val service: OpenApiMainService,
    private val cache: AccountDao,
    private val serverMsgTranslator: ServerMsgTranslator
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        page: Int,
    ): Flow<DataState<List<Account>>> = flow {
        emit(DataState.loading<List<Account>>())
        if (authToken == null) {
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        // get Tasks from network
        try { // catch network exception
            val users = service.getAllUsers(
                "${authToken.token}",
                skip = (page - 1) * Constants.PAGINATION_PAGE_SIZE,
                limit = Constants.PAGINATION_PAGE_SIZE
            ).results.map { it.toAccount() }

            // Insert into cache
            for (task in users) {
                try {
                    cache.insert(task.toEntity())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            var keepSearching = true;
            while(keepSearching){
                val cachedUsers = cache.getAllAccounts(
                    page = page
                )
                val cachedUsersSize = cachedUsers.size

                for(cachedUser in cachedUsers){
                    try { // try to load each task and check if it exists on the server
                        val serverAccount = service.getAccountById(
                            "${authToken.token}",
                            id = cachedUser._id
                        )
                        // If task was not found on server, delete task from cache.
                        if(serverAccount?.error?.contains(ErrorHandling.ERROR_USER_DOES_NOT_EXIST) == true) {
                            cache.deleteTask(cachedUser._id)
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

        } catch (e: Exception) {
            emit(
                DataState.error<List<Account>>(
                    response = Response(
                        message = "Unable get all users.",
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











