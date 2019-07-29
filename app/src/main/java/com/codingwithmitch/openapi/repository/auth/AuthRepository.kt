package com.codingwithmitch.openapi.repository.auth

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.codingwithmitch.openapi.api.auth.OpenApiAuthService
import com.codingwithmitch.openapi.api.auth.network_responses.LoginResponse
import com.codingwithmitch.openapi.api.auth.network_responses.RegistrationResponse
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.persistence.AccountPropertiesDao
import com.codingwithmitch.openapi.persistence.AuthTokenDao
import com.codingwithmitch.openapi.ui.auth.state.AuthState
import com.codingwithmitch.openapi.util.PreferenceKeys
import retrofit2.Response
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sharedPrefsEditor: SharedPreferences.Editor
    )
{
    private val TAG: String = "AppDebug"

    fun attemptRegistration(email: String, username: String, password: String, confirmPassword: String): LiveData<AuthState> {

        return object: AuthNetworkBoundResource<RegistrationResponse>() {

            override suspend fun createResponse(): Response<RegistrationResponse> {
                return openApiAuthService.register(email,username,password, confirmPassword)
            }

            override fun saveUserToPrefs(email: String) {
                saveAuthenticatedUserToPrefs(email)
            }

            override suspend fun saveTokenLocally(authToken: AuthToken): Long {
                return authTokenDao.insert(authToken)
            }

            override suspend fun saveAccountPropertiesLocally(accountProperties: AccountProperties): Long {
                return accountPropertiesDao.insert(accountProperties)
            }

        }.asLiveData()
    }

    fun attemptLogin(email: String, password: String): LiveData<AuthState> {

        return object: AuthNetworkBoundResource<LoginResponse>() {

            override suspend fun createResponse(): Response<LoginResponse> {
                return openApiAuthService.login(email.toLowerCase(), password)
            }

            override fun saveUserToPrefs(email: String) {
                saveAuthenticatedUserToPrefs(email)
            }

            override suspend fun saveTokenLocally(authToken: AuthToken): Long {
                return authTokenDao.insert(authToken)
            }

            override suspend fun saveAccountPropertiesLocally(accountProperties: AccountProperties): Long {
                return accountPropertiesDao.insert(accountProperties)
            }

        }.asLiveData()
    }

    suspend fun retrieveTokenFromLocalDb(pk: Int): AuthToken? {
        return authTokenDao.searchByPk(pk)
    }

    suspend fun retrieveAccountPropertiesUsingEmail(email: String): AccountProperties?{
        return accountPropertiesDao.searchByEmail(email)
    }


    fun saveAuthenticatedUserToPrefs(email: String){
        sharedPrefsEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        sharedPrefsEditor.apply()
    }

}



















