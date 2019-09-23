package com.codingwithmitch.openapi.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.codingwithmitch.openapi.util.ApiSuccessResponse
import com.codingwithmitch.openapi.util.GenericApiResponse
import com.codingwithmitch.openapi.api.auth.OpenApiAuthService
import com.codingwithmitch.openapi.api.auth.network_responses.*
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.persistence.AccountPropertiesDao
import com.codingwithmitch.openapi.persistence.AuthTokenDao
import com.codingwithmitch.openapi.repository.JobManager
import com.codingwithmitch.openapi.repository.NetworkBoundResource
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.Response
import com.codingwithmitch.openapi.ui.ResponseType
import com.codingwithmitch.openapi.ui.auth.state.AuthViewState
import com.codingwithmitch.openapi.ui.auth.state.LoginFields
import com.codingwithmitch.openapi.ui.auth.state.RegistrationFields
import com.codingwithmitch.openapi.util.AbsentLiveData
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.ERROR_SAVE_ACCOUNT_PROPERTIES
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.codingwithmitch.openapi.util.PreferenceKeys
import com.codingwithmitch.openapi.util.SuccessHandling.NetworkSuccessResponses.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.*
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val sessionManager: SessionManager,
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor
    )
{
    private val TAG: String = "AppDebug"

    private val jobManager: JobManager = JobManager()

    fun attemptLogin(email: String, password: String) : LiveData<DataState<AuthViewState>>{

        val loginFieldErrors = LoginFields(email, password).isValidForLogin()
        if(!loginFieldErrors.equals(LoginFields.LoginError.none())){
            return returnErrorResponse(loginFieldErrors, ResponseType.Dialog())
        }

        return object: NetworkBoundResource<LoginResponse, Void, AuthViewState>(
            "attemptLogin",
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ) {

            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {

                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                // Incorrect login credentials counts as a 200 response from server, so need to handle that
                if(response.body.response.equals(GENERIC_AUTH_ERROR)){
                    return onReturnError(response.body.errorMessage, true, false)
                }

                // Don't care about result here. Just insert if it doesn't exist b/c of foreign key relationship
                // with AuthToken
                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )

                // will return -1 if failure
                val result2 = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )
                if(result2 < 0){
                    return onCompleteJob(DataState.error(
                        Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog()))
                    )
                }

                saveAuthenticatedUserToPrefs(email)
                onCompleteJob(
                    DataState.data(
                        AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        ),
                        response = null
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email, password)
            }

            // not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            // not used in this case
            override suspend fun updateLocalDb(cacheObject: Void?) {

            }

            override fun setJob(job: Job) {
                jobManager.addJob(methodName, job)
            }

        }.asLiveData()
    }

    fun attemptRegistration(email: String, username: String, password: String, confirmPassword: String): LiveData<DataState<AuthViewState>> {

        val registrationFieldErrors = RegistrationFields(email, username, password, confirmPassword).isValidForRegistration()
        if(!registrationFieldErrors.equals(RegistrationFields.RegistrationError.none())){
            return returnErrorResponse(registrationFieldErrors, ResponseType.Dialog())
        }

        return object: NetworkBoundResource<RegistrationResponse, Void, AuthViewState>(
            "attemptRegistration",
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){

            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }


            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {

                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                // Credentials that are already in use will result in 200 code so need to handle that
                if(response.body.response.equals(GENERIC_AUTH_ERROR)){
                    return onReturnError(response.body.errorMessage, true, false)
                }

                // The following situations will still return a 200 code:
                // 1) Email is already in use
                // 2) username is already in use
                // 3) Passwords don't match
                if(response.body.response.equals(GENERIC_AUTH_ERROR)){
                    onCompleteJob(DataState.error(
                        Response(response.body.errorMessage, ResponseType.Dialog()))
                    )
                    return
                }

                val result1 = accountPropertiesDao.insertAndReplace(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        response.body.username
                    )
                )
                // will return -1 if failure
                if(result1 < 0){
                    onCompleteJob(DataState.error(
                        Response(ERROR_SAVE_ACCOUNT_PROPERTIES, ResponseType.Dialog()))
                    )
                    return
                }

                // will return -1 if failure
                val result2 = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )
                if(result2 < 0){
                    onCompleteJob(DataState.error(
                        Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                    ))
                    return
                }

                saveAuthenticatedUserToPrefs(email)
                onCompleteJob(
                    DataState.data(
                        AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)),
                            response = null
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openApiAuthService.register(email, username, password, confirmPassword)
            }

            // not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            // not used in this case
            override suspend fun updateLocalDb(cacheObject: Void?) {

            }

            override fun setJob(job: Job) {
                jobManager.addJob(methodName, job)
            }


        }.asLiveData()
    }


    fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>>{

        val previousAuthUserEmail: String? = sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)

        if(previousAuthUserEmail.isNullOrBlank()){
            Log.d(TAG, "checkPreviousAuthUser: No previously authenticated user found.")
            return returnNoTokenFound()
        }
        else{
            return object: NetworkBoundResource<Void, AccountProperties, AuthViewState>(
                "checkPreviousAuthUser",
                sessionManager.isConnectedToTheInternet(),
                false,
                false,
                false
            ){

                override suspend fun createCacheRequestAndReturn() {
                    accountPropertiesDao.searchByEmail(previousAuthUserEmail).let { accountProperties ->
                        Log.d(TAG, "createCacheRequestAndReturn: searching for token... account properties: ${accountProperties}")

                        if (accountProperties == null) {
                            Log.d(TAG, "createCacheRequestAndReturn: AccountProperties for email ${previousAuthUserEmail} are null. " +
                                    "Completing job...")
                            onCompleteJob(
                                DataState.data(
                                    null,
                                    Response(
                                        RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                        ResponseType.None()
                                    )
                                )
                            )
                        }
                        else{
                            if(accountProperties.pk > -1){
                                authTokenDao.searchByPk(accountProperties.pk).let { authToken ->
                                    if(authToken != null){
                                        if(authToken.token != null){
                                            onCompleteJob(
                                                DataState.data(
                                                    AuthViewState(authToken = authToken),
                                                    null
                                                )
                                            )
                                        }
                                        else{
                                            Log.d(TAG, "createCacheRequestAndReturn: token was null. Completing job...")
                                            onCompleteJob(
                                                DataState.data(
                                                    null,
                                                    Response(
                                                        RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                                        ResponseType.None()
                                                    )
                                                )
                                            )
                                        }
                                    }
                                    else{
                                        Log.d(TAG, "createCacheRequestAndReturn: AuthToken was null. Completing job...")
                                        onCompleteJob(
                                            DataState.data(
                                                null,
                                                Response(
                                                    RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                                    ResponseType.None()
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                            else{
                                Log.d(TAG, "createCacheRequestAndReturn: PK was null. Completing job...")
                                onCompleteJob(
                                    DataState.data(
                                        null,
                                        Response(
                                            RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                            ResponseType.None()
                                        )
                                    )
                                )
                            }
                        }
                    }
                }

                // not used in this case
                override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Void>) {
                }

                // not used in this case
                override fun createCall(): LiveData<GenericApiResponse<Void>> {
                    return AbsentLiveData.create()
                }

                // not used in this case
                override fun loadFromCache(): LiveData<AuthViewState> {
                    return AbsentLiveData.create()
                }

                // not used in this case
                override suspend fun updateLocalDb(cacheObject: AccountProperties?) {
                }

                override fun setJob(job: Job) {
                    jobManager.addJob(methodName, job)
                }

            }.asLiveData()
        }
    }

    private fun returnErrorResponse(errorMessage: String, responseType: ResponseType): LiveData<DataState<AuthViewState>>{
        return object: LiveData<DataState<AuthViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.error(Response(errorMessage, responseType))
            }
        }
    }

    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>>{
        return object: LiveData<DataState<AuthViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.data(null, Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE, ResponseType.None()))
            }
        }
    }

    fun saveAuthenticatedUserToPrefs(email: String){
        sharedPrefsEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        sharedPrefsEditor.apply()
    }


    fun cancelActiveJobs(){
        Log.d(TAG, "AuthRepository: cancelling on-going jobs... ")
        jobManager.cancelActiveJobs()
    }

}

























