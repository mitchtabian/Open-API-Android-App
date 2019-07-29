package com.codingwithmitch.openapi.repository.main

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
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class MainRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val sharedPrefsEditor: SharedPreferences.Editor
    )
{
    private val TAG: String = "AppDebug"


    suspend fun logout(pk: Int): Int{
        return authTokenDao.updateToken(pk)
    }
}



















