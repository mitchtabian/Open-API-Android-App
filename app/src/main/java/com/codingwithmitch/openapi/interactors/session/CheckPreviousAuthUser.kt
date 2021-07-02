package com.codingwithmitch.openapi.interactors.session

import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.persistence.account.AccountDao
import com.codingwithmitch.openapi.persistence.auth.AuthTokenDao
import com.codingwithmitch.openapi.persistence.auth.toAuthToken
import com.codingwithmitch.openapi.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Attempt to authenticate as soon as the user launches the app.
 * If no user was authenticated in a previous session then do nothing.
 */
class CheckPreviousAuthUser(
    private val accountDao: AccountDao,
    private val authTokenDao: AuthTokenDao,
) {
    fun execute(
        email: String,
    ): Flow<DataState<AuthToken>> = flow {
        var authToken: AuthToken? = null
        try{
            val entity = accountDao.searchByEmail(email)
            if(entity != null){
                authToken = authTokenDao.searchByPk(entity.pk)?.toAuthToken()
                if(authToken != null){
                    emit(DataState.data(response = null, data = authToken))
                }
            }
            if(authToken == null){
                throw Exception("No previously authenticated user. This error can be ignored.")
            }
        }catch (e: Exception){
            e.printStackTrace()
            emitNoPreviousAuthUser()
        }
    }

    /**
     * If no user was previously authenticated then emit this error. The UI is waiting for it.
     */
    private fun emitNoPreviousAuthUser(): Flow<DataState<AuthToken>> = flow{
        emit(DataState.error<AuthToken>(
            response = Response(
                SuccessHandling.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                UIComponentType.None(),
                MessageType.Error()
            )
        ))
    }
}












