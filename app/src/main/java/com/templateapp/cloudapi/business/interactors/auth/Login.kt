package com.templateapp.cloudapi.business.interactors.auth

import com.templateapp.cloudapi.business.datasource.network.auth.OpenApiAuthService
import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.datasource.cache.account.AccountDao
import com.templateapp.cloudapi.business.datasource.cache.account.toEntity
import com.templateapp.cloudapi.business.datasource.cache.auth.AuthTokenDao
import com.templateapp.cloudapi.business.datasource.cache.auth.toEntity
import com.templateapp.cloudapi.business.datasource.datastore.AppDataStore
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.templateapp.cloudapi.presentation.util.DataStoreKeys
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class Login(
    private val service: OpenApiAuthService,
    private val accountDao: AccountDao,
    private val authTokenDao: AuthTokenDao,
    private val appDataStoreManager: AppDataStore,
    private val serverMsgTranslator: ServerMsgTranslator
){
    fun execute(
        email: String,
        password: String,
    ): Flow<DataState<AuthToken>> = flow {
        emit(DataState.loading<AuthToken>())
        val loginResponse = service.login(email, password)
        // Incorrect login credentials counts as a 200 response from server, so need to handle that
        if(loginResponse.errorMessage == ErrorHandling.INVALID_CREDENTIALS){
            throw Exception(ErrorHandling.INVALID_CREDENTIALS)
        }

        // cache the Account information (don't know the username yet)
        accountDao.insertOrIgnore(
            Account(
                _id = loginResponse.user._id,
                email = loginResponse.user.email,
                name = loginResponse.user.name,
                age = loginResponse.user.age,
                createdAt = loginResponse.user.createdAt,
                updatedAt = loginResponse.user.updatedAt,
                userCreatedSequence = loginResponse.user.userCreatedSequence,
                __v = loginResponse.user.__v,
                role = loginResponse.user.role,
                enabled = loginResponse.user.enabled

            ).toEntity()
        )

        // cache the auth token
        val authToken = AuthToken(
            loginResponse.user._id,
            loginResponse.token
        )
        val result = authTokenDao.insert(authToken.toEntity())
        // can't proceed unless token can be cached
        if(result < 0){
            throw Exception(ERROR_SAVE_AUTH_TOKEN)
        }
        // save authenticated user to datastore for auto-login next time
        appDataStoreManager.setValue(DataStoreKeys.PREVIOUS_AUTH_USER, email)
        emit(DataState.data(data = authToken, response = null))
    }.catch { e ->
        emit(handleUseCaseException(e, serverMsgTranslator))
    }
}














