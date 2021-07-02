package com.codingwithmitch.openapi.interactors.account

import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.api.main.toAccount
import com.codingwithmitch.openapi.models.Account
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.persistence.account.AccountDao
import com.codingwithmitch.openapi.persistence.account.toAccount
import com.codingwithmitch.openapi.persistence.account.toEntity
import com.codingwithmitch.openapi.util.DataState
import com.codingwithmitch.openapi.util.MessageType
import com.codingwithmitch.openapi.util.Response
import com.codingwithmitch.openapi.util.UIComponentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class GetAccount(
    private val service: OpenApiMainService,
    private val cache: AccountDao,
) {
    fun execute(
        authToken: AuthToken?,
    ): Flow<DataState<Account>> = flow {
        emit(DataState.loading<Account>())
        try {
            if(authToken == null){
                throw Exception("Authentication token is invalid. Log out and log back in.")
            }
            // get from network
            val account = service.getAccount("Token ${authToken.token!!}").toAccount()

            // update/insert into the cache
            cache.insertAndReplace(account.toEntity())

            // emit from cache
            val cachedAccount = cache.searchByPk(account.pk).toAccount()

            emit(DataState.data(response = null, cachedAccount))
        }catch (e: Exception){
            e.printStackTrace()
            emit(DataState.error<Account>(
                response = Response(
                    message = e.message,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            ))
        }
    }
}















