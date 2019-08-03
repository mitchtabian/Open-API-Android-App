package com.codingwithmitch.openapi.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.liveData
import com.codingwithmitch.openapi.api.auth.OpenApiAuthService
import com.codingwithmitch.openapi.api.auth.network_responses.*
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.persistence.AccountPropertiesDao
import com.codingwithmitch.openapi.persistence.AuthTokenDao
import com.codingwithmitch.openapi.ui.auth.state.AuthScreenState.*
import com.codingwithmitch.openapi.util.PreferenceKeys
import retrofit2.HttpException
import java.lang.Exception
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
    private val ERROR_RESPONSE = "Error"


    fun attemptRegistration(email: String, username: String, password: String, confirmPassword: String) = liveData {
        emit(Loading)
        try{
            val response: RegistrationResponse = openApiAuthService.register(email,username,password, confirmPassword)

            if(response.response.equals(ERROR_RESPONSE) ){
                emit(Error(response.errorMessage))
            }
            else {
                try{
                    if(accountPropertiesDao.insert(AccountProperties(response.pk, response.email, response.username)) > -1){
                        // AccountProperties insert success
                        if(authTokenDao.insert(AuthToken(response.pk, response.token)) > -1){
                            // AccountProperties insert success
                            emit(Data(AuthToken(response.pk, response.token)))
                            saveAuthenticatedUserToPrefs(email)
                        }
                        else{
                            // insert fail
                            emit(Error("Error saving authentication token.\nTry restarting the app."))
                        }
                    }

                }catch (e: Exception){
                    Log.e(TAG, "onAuthenticationSuccess: ${e.message}")
                    emit(Error(extractErrorMessage(e.message)))
                }
            }

        }catch (e: Throwable) {
            Log.e(TAG, "registration2: ${e.message}")
            emit(Error(extractErrorMessage(e.message)))
        } catch (e: HttpException) {
            Log.e(TAG, "registration2: ${e.message}")
            emit(Error(extractErrorMessage(e.message)))
        }
    }


    fun attemptLogin(email: String, password: String) = liveData{
        emit(Loading)
        try{
            val response: LoginResponse = openApiAuthService.login(email.toLowerCase(), password)

            if(response.response.equals(ERROR_RESPONSE) ){
                emit(Error(response.errorMessage))
            }
            else {
                try{
                    if(accountPropertiesDao.insert(AccountProperties(response.pk, response.email, "")) > -1){
                        // AccountProperties insert success
                        if(authTokenDao.insert(AuthToken(response.pk, response.token)) > -1){
                            // AccountProperties insert success
                            emit(Data(AuthToken(response.pk, response.token)))
                            saveAuthenticatedUserToPrefs(email)
                        }
                        else{
                            // insert fail
                            emit(Error("Error saving authentication token.\nTry restarting the app."))
                        }
                    }

                }catch (e: Exception){
                    Log.e(TAG, "onAuthenticationSuccess: ${e.message}")
                    emit(Error(extractErrorMessage(e.message)))
                }
            }

        }catch (e: Throwable) {
            Log.e(TAG, "login2: ${e.message}")
            emit(Error(extractErrorMessage(e.message)))
        } catch (e: HttpException) {
            Log.e(TAG, "login2: ${e.message}")
            emit(Error(extractErrorMessage(e.message)))
        }
    }
    private fun extractErrorMessage(message: String?): String{
        if(!message.isNullOrEmpty()){
            return message
        }
        return "Unknown error."
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



















