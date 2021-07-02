package com.codingwithmitch.openapi.interactors.account

import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.persistence.account.AccountDao
import com.codingwithmitch.openapi.util.*
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.SUCCESS_ACCOUNT_UPDATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class UpdateAccount(
    private val service: OpenApiMainService,
    private val cache: AccountDao,
) {
    fun execute(
        authToken: AuthToken?,
        pk: Int?,
        email: String,
        username: String,
    ): Flow<DataState<Response>> = flow {
        emit(DataState.loading<Response>())
        try {
            if(authToken == null){
                throw Exception("Authentication token is invalid. Log out and log back in.")
            }
            if(pk == null){
                throw Exception("Account PK is invalid. Log out and log back in.")
            }
            // Update network
            val response = service.updateAccount(
                authorization = "Token ${authToken.token!!}",
                email = email,
                username = username
            )

            if(response.response != SUCCESS_ACCOUNT_UPDATED){
                throw Exception("Unable to update account. Try logging out and logging back in.")
            }

            // update cache
            cache.updateAccount(
                pk = pk,
                email = email,
                username = username
            )

            // Tell the UI it was successful
            emit(DataState.data<Response>(
                data = Response(
                    message = SUCCESS_ACCOUNT_UPDATED,
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ),
                response = null
            ))
        }catch (e: Exception){
            e.printStackTrace()
            emit(DataState.error<Response>(
                response = Response(
                    message = e.message,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            ))
        }
    }
}
