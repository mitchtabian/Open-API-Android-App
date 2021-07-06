package com.codingwithmitch.openapi.interactors.account

import com.codingwithmitch.openapi.api.handleUseCaseException
import com.codingwithmitch.openapi.models.Account
import com.codingwithmitch.openapi.persistence.account.AccountDao
import com.codingwithmitch.openapi.persistence.account.toAccount
import com.codingwithmitch.openapi.util.DataState
import com.codingwithmitch.openapi.util.MessageType
import com.codingwithmitch.openapi.util.Response
import com.codingwithmitch.openapi.util.UIComponentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

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















