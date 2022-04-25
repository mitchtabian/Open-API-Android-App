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
import com.templateapp.cloudapi.business.datasource.cache.task.toEntity
import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.domain.util.DataState
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_UNABLE_TO_RETRIEVE_ACCOUNT_DETAILS
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class GetAllUsers(
    private val service: OpenApiMainService,
    private val serverMsgTranslator: ServerMsgTranslator
) {

    private val TAG: String = "AppDebug"

    fun execute(
    ): Flow<DataState<String>> = flow {
        emit(DataState.loading<String>())
        // get from network
        print("EVO ME")
        val users = service.getAllUsers(
        ).count.map { it.toString() }

       /* for(user in users){
            try{
                accountCache.insert(user.toEntity())
            }catch (e: Exception){
                e.printStackTrace()
            }
        }*/

        emit(DataState.data(response = null, data = "ffffff"))
    }.catch { e ->
        emit(handleUseCaseException(e, serverMsgTranslator))
    }
}









