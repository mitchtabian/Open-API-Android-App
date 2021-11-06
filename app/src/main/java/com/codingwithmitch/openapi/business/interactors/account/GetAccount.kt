package com.codingwithmitch.openapi.business.interactors.account

import com.codingwithmitch.openapi.api.handleUseCaseException
import com.codingwithmitch.openapi.business.datasource.network.main.OpenApiMainService
import com.codingwithmitch.openapi.business.datasource.network.main.toAccount
import com.codingwithmitch.openapi.business.domain.models.Account
import com.codingwithmitch.openapi.business.domain.models.AuthToken
import com.codingwithmitch.openapi.business.datasource.cache.account.AccountDao
import com.codingwithmitch.openapi.business.datasource.cache.account.toAccount
import com.codingwithmitch.openapi.business.datasource.cache.account.toEntity
import com.codingwithmitch.openapi.business.datasource.cache.auth.AuthTokenDao
import com.codingwithmitch.openapi.business.datasource.cache.auth.toEntity
import com.codingwithmitch.openapi.business.domain.util.DataState
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling.Companion.ERROR_UNABLE_TO_RETRIEVE_ACCOUNT_DETAILS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class GetAccount(
    private val service: OpenApiMainService,
    private val accountCache: AccountDao,
    private val tokenCache: AuthTokenDao
) {
    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
    ): Flow<DataState<Account>> = flow {
        emit(DataState.loading<Account>())
        if(authToken == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        // get from network
        val account = service.getAccount("Token ${authToken.token}").toAccount()

        // update/insert into the cache
        accountCache.insertAndReplace(account.toEntity())

        // Did insertAndReplace cause token to be deleted? If so, write token back to the database.
        if(tokenCache.searchByPk(account.pk)==null) {
            tokenCache.insert(authToken.toEntity())
        }

        // emit from cache
        val cachedAccount = accountCache.searchByPk(account.pk)?.toAccount()

        if(cachedAccount == null){
            throw Exception(ERROR_UNABLE_TO_RETRIEVE_ACCOUNT_DETAILS)
        }

        emit(DataState.data(response = null, cachedAccount))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}















