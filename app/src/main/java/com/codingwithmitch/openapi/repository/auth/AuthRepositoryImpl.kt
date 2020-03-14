package com.codingwithmitch.openapi.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.codingwithmitch.openapi.api.auth.OpenApiAuthService
import com.codingwithmitch.openapi.api.auth.network_responses.LoginResponse
import com.codingwithmitch.openapi.api.auth.network_responses.RegistrationResponse
import com.codingwithmitch.openapi.di.auth.AuthScope
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistence.AccountPropertiesDao
import com.codingwithmitch.openapi.persistence.AuthTokenDao
import com.codingwithmitch.openapi.repository.NetworkBoundResource
import com.codingwithmitch.openapi.repository.emitError
import com.codingwithmitch.openapi.repository.safeApiCall
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.auth.state.AuthViewState
import com.codingwithmitch.openapi.ui.auth.state.LoginFields
import com.codingwithmitch.openapi.ui.auth.state.RegistrationFields
import com.codingwithmitch.openapi.util.*
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.ERROR_SAVE_ACCOUNT_PROPERTIES
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.INVALID_CREDENTIALS
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AuthScope
class AuthRepositoryImpl
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor
): AuthRepository
{

    private val TAG: String = "AppDebug"

    override fun attemptLogin(
        stateEvent: StateEvent,
        email: String,
        password: String
    ): Flow<DataState<AuthViewState>> = flow {

        val loginFieldErrors = LoginFields(email, password).isValidForLogin()
        if(loginFieldErrors.equals(LoginFields.LoginError.none())){

            val apiResult = safeApiCall(IO){
                openApiAuthService.login(email, password)
            }
            emit(
                object: ApiResponseHandler<AuthViewState, LoginResponse>(
                    response = apiResult,
                    stateEvent = stateEvent
                ) {
                    override fun handleSuccess(resultObj: LoginResponse): DataState<AuthViewState> {
                        // Incorrect login credentials counts as a 200 response from server, so need to handle that
                        if(resultObj.response.equals(GENERIC_AUTH_ERROR)){
                            return DataState.error(
                                response = Response(
                                    INVALID_CREDENTIALS,
                                    UIComponentType.Dialog(),
                                    MessageType.Error()
                                ),
                                stateEvent = stateEvent
                            )
                        }

                        CoroutineScope(IO).launch {

                            accountPropertiesDao.insertOrIgnore(
                                AccountProperties(
                                    resultObj.pk,
                                    resultObj.email,
                                    ""
                                )
                            )

                            // will return -1 if failure
                            val result = authTokenDao.insert(
                                AuthToken(
                                    resultObj.pk,
                                    resultObj.token
                                )
                            )
                            if(result < 0){
                                return DataState.error(
                                    response = Response(
                                        INVALID_CREDENTIALS,
                                        UIComponentType.Dialog(),
                                        MessageType.Error()
                                    ),
                                    stateEvent = stateEvent
                                )
//                               onCompleteJob(
//                                    DataState.error(
//                                        Response(
//                                            ERROR_SAVE_AUTH_TOKEN,
//                                            ResponseType.Dialog()
//                                        )
//                                    )
//                                )
                            }
                        }


                    }

                }.result
            )
        }
        else{
            emitError<AuthViewState>(
                loginFieldErrors,
                UIComponentType.Dialog(),
                stateEvent
            )
        }
    }

    override fun attemptRegistration(
        stateEvent: StateEvent,
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): Flow<DataState<AuthViewState>> = flow {

    }


    override fun checkPreviousAuthUser(
        stateEvent: StateEvent
    ): Flow<DataState<AuthViewState>> = flow{

    }

    override fun saveAuthenticatedUserToPrefs(email: String) = flow {

    }

    override fun returnNoTokenFound(
        stateEvent: StateEvent
    ): Flow<DataState<AuthViewState>> = flow{

        emitError<AuthViewState>(
            RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
            UIComponentType.Dialog(),
            stateEvent
        )
    }



//    // NETWORK ONLY
//    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>>{
//
//        val loginFieldErrors = LoginFields(email, password).isValidForLogin()
//        if(!loginFieldErrors.equals(LoginFields.LoginError.none())){
//            return returnErrorResponse(loginFieldErrors, ResponseType.Dialog())
//        }
//
//
//
//        return object: NetworkBoundResource<LoginResponse, Any, AuthViewState>(
//            sessionManager.isConnectedToTheInternet(),
//            true,
//            true,
//            false
//        ){
//
//            // Ignore
//            override fun loadFromCache(): LiveData<AuthViewState> {
//                return AbsentLiveData.create()
//            }
//
//            // Ignore
//            override suspend fun updateLocalDb(cacheObject: Any?) {
//
//            }
//
//            // not used in this case
//            override suspend fun createCacheRequestAndReturn() {
//
//            }
//
//            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
//                Log.d(TAG, "handleApiSuccessResponse: ${response}")
//
//                // Incorrect login credentials counts as a 200 response from server, so need to handle that
//                if(response.body.response.equals(GENERIC_AUTH_ERROR)){
//                    return onErrorReturn(response.body.errorMessage, true, false)
//                }
//
//                // Don't care about result here. Just insert if it doesn't exist b/c of foreign key relationship
//                // with AuthToken
//                accountPropertiesDao.insertOrIgnore(
//                    AccountProperties(
//                        response.body.pk,
//                        response.body.email,
//                        ""
//                    )
//                )
//
//                // will return -1 if failure
//                val result = authTokenDao.insert(
//                    AuthToken(
//                        response.body.pk,
//                        response.body.token
//                    )
//                )
//                if(result < 0){
//                    return onCompleteJob(
//                        DataState.error(
//                            Response(
//                                ERROR_SAVE_AUTH_TOKEN,
//                                ResponseType.Dialog()
//                            )
//                        )
//                    )
//                }
//
//                saveAuthenticatedUserToPrefs(email)
//
//                onCompleteJob(
//                    DataState.data(
//                        data = AuthViewState(
//                            authToken = AuthToken(response.body.pk, response.body.token)
//                        )
//                    )
//                )
//            }
//
//            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
//                return openApiAuthService.login(email, password)
//            }
//
//            override fun setJob(job: Job) {
//                addJob("attemptLogin", job)
//            }
//
//        }.asLiveData()
//    }
//
//    fun attemptRegistration(
//        email: String,
//        username: String,
//        password: String,
//        confirmPassword: String
//    ): LiveData<DataState<AuthViewState>>{
//
//        val registrationFieldErrors = RegistrationFields(email, username, password, confirmPassword).isValidForRegistration()
//        if(!registrationFieldErrors.equals(RegistrationFields.RegistrationError.none())){
//            return returnErrorResponse(registrationFieldErrors, ResponseType.Dialog())
//        }
//
//        return object: NetworkBoundResource<RegistrationResponse, Any, AuthViewState>(
//            sessionManager.isConnectedToTheInternet(),
//            true,
//            true,
//            false
//        ){
//            // Ignore
//            override fun loadFromCache(): LiveData<AuthViewState> {
//                return AbsentLiveData.create()
//            }
//
//            // Ignore
//            override suspend fun updateLocalDb(cacheObject: Any?) {
//
//            }
//
//            // not used in this case
//            override suspend fun createCacheRequestAndReturn() {
//
//            }
//
//            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
//
//                Log.d(TAG, "handleApiSuccessResponse: ${response}")
//
//                if(response.body.response.equals(GENERIC_AUTH_ERROR)){
//                    return onErrorReturn(response.body.errorMessage, true, false)
//                }
//
//                val result1 = accountPropertiesDao.insertAndReplace(
//                    AccountProperties(
//                        response.body.pk,
//                        response.body.email,
//                        response.body.username
//                    )
//                )
//
//                // will return -1 if failure
//                if(result1 < 0){
//                    onCompleteJob(
//                        DataState.error(
//                            Response(
//                                ERROR_SAVE_ACCOUNT_PROPERTIES,
//                                ResponseType.Dialog()
//                            )
//                        )
//                    )
//                    return
//                }
//
//                // will return -1 if failure
//                val result2 = authTokenDao.insert(
//                    AuthToken(
//                        response.body.pk,
//                        response.body.token
//                    )
//                )
//                if(result2 < 0){
//                    onCompleteJob(
//                        DataState.error(
//                            Response(
//                                ERROR_SAVE_AUTH_TOKEN,
//                                ResponseType.Dialog()
//                            )
//                        ))
//                    return
//                }
//
//                saveAuthenticatedUserToPrefs(email)
//
//                onCompleteJob(
//                    DataState.data(
//                        data = AuthViewState(
//                            authToken = AuthToken(response.body.pk, response.body.token)
//                        )
//                    )
//                )
//            }
//
//            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
//                return openApiAuthService.register(email, username, password, confirmPassword)
//            }
//
//            override fun setJob(job: Job) {
//                addJob("attemptRegistration", job)
//            }
//
//        }.asLiveData()
//    }
//
//
//    fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>>{
//
//        val previousAuthUserEmail: String? = sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)
//
//        if(previousAuthUserEmail.isNullOrBlank()){
//            Log.d(TAG, "checkPreviousAuthUser: No previously authenticated user found.")
//            return returnNoTokenFound()
//        }
//        else{
//            return object: NetworkBoundResource<Void, Any, AuthViewState>(
//                sessionManager.isConnectedToTheInternet(),
//                false,
//                false,
//                false
//            ){
//
//                // Ignore
//                override fun loadFromCache(): LiveData<AuthViewState> {
//                    return AbsentLiveData.create()
//                }
//
//                // Ignore
//                override suspend fun updateLocalDb(cacheObject: Any?) {
//
//                }
//
//                override suspend fun createCacheRequestAndReturn() {
//                    accountPropertiesDao.searchByEmail(previousAuthUserEmail).let { accountProperties ->
//                        Log.d(TAG, "createCacheRequestAndReturn: searching for token... account properties: ${accountProperties}")
//
//                        accountProperties?.let {
//                            if(accountProperties.pk > -1){
//                                authTokenDao.searchByPk(accountProperties.pk).let { authToken ->
//                                    if(authToken != null){
//                                        if(authToken.token != null){
//                                            onCompleteJob(
//                                                DataState.data(
//                                                    AuthViewState(authToken = authToken)
//                                                )
//                                            )
//                                            return
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        Log.d(TAG, "createCacheRequestAndReturn: AuthToken not found...")
//                        onCompleteJob(
//                            DataState.data(
//                                null,
//                                Response(
//                                    RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
//                                    ResponseType.None()
//                                )
//                            )
//                        )
//                    }
//                }
//
//                // not used in this case
//                override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Void>) {
//                }
//
//                // not used in this case
//                override fun createCall(): LiveData<GenericApiResponse<Void>> {
//                    return AbsentLiveData.create()
//                }
//
//                override fun setJob(job: Job) {
//                    addJob("checkPreviousAuthUser", job)
//                }
//
//
//            }.asLiveData()
//        }
//    }
//
//    private fun saveAuthenticatedUserToPrefs(email: String){
//        sharedPrefsEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
//        sharedPrefsEditor.apply()
//    }
//
//    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>>{
//        return object: LiveData<DataState<AuthViewState>>(){
//            override fun onActive() {
//                super.onActive()
//                value = DataState.data(null,
//                    Response(
//                        RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
//                        ResponseType.None()
//                    )
//                )
//            }
//        }
//    }
//
//    private fun returnErrorResponse(errorMessage: String, responseType: ResponseType): LiveData<DataState<AuthViewState>>{
//        Log.d(TAG, "returnErrorResponse: ${errorMessage}")
//
//        return object: LiveData<DataState<AuthViewState>>(){
//            override fun onActive() {
//                super.onActive()
//                value = DataState.error(
//                    Response(
//                        errorMessage,
//                        responseType
//                    )
//                )
//            }
//        }
//    }

}