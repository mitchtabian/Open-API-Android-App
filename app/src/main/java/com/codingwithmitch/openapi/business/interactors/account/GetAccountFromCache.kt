package com.codingwithmitch.openapi.business.interactors.account

import com.codingwithmitch.openapi.api.handleUseCaseException
import com.codingwithmitch.openapi.business.domain.models.Account
import com.codingwithmitch.openapi.business.datasource.cache.account.AccountDao
import com.codingwithmitch.openapi.business.datasource.cache.account.toAccount
import com.codingwithmitch.openapi.business.domain.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetAccountFromCache(
    private val cache: AccountDao,
) {
    fun execute(
        pk: Int,
    ): Flow<DataState<Account>> = flow {
        emit(DataState.loading<Account>())
        // emit from cache
        val cachedAccount = cache.searchByPk(pk).toAccount()

        emit(DataState.data(response = null, cachedAccount))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}















