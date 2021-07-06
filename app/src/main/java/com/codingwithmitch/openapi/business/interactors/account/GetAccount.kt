package com.codingwithmitch.openapi.business.interactors.account

import com.codingwithmitch.openapi.api.handleUseCaseException
import com.codingwithmitch.openapi.business.datasource.network.main.OpenApiMainService
import com.codingwithmitch.openapi.business.datasource.network.main.toAccount
import com.codingwithmitch.openapi.business.domain.models.Account
import com.codingwithmitch.openapi.business.domain.models.AuthToken
import com.codingwithmitch.openapi.business.datasource.cache.account.AccountDao
import com.codingwithmitch.openapi.business.datasource.cache.account.toAccount
import com.codingwithmitch.openapi.business.datasource.cache.account.toEntity
import com.codingwithmitch.openapi.business.domain.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class GetAccount(
    private val service: OpenApiMainService,
    private val cache: AccountDao,
) {
    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
    ): Flow<DataState<Account>> = flow {
        emit(DataState.loading<Account>())
        if(authToken == null){
            throw Exception("Authentication token is invalid. Log out and log back in.")
        }
        // get from network
        val account = service.getAccount("Token ${authToken.token}").toAccount()

        // update/insert into the cache
        cache.insertAndReplace(account.toEntity())

        // emit from cache
        val cachedAccount = cache.searchByPk(account.pk).toAccount()

        emit(DataState.data(response = null, cachedAccount))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}















