package com.codingwithmitch.openapi.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.codingwithmitch.openapi.api.ApiSuccessResponse
import com.codingwithmitch.openapi.api.GenericApiResponse
import com.codingwithmitch.openapi.api.GenericResponse
import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.persistence.AccountPropertiesDao
import com.codingwithmitch.openapi.repository.NetworkBoundResource
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.Response
import com.codingwithmitch.openapi.ui.main.account.state.AccountViewState
import com.codingwithmitch.openapi.util.*
import kotlinx.coroutines.*
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
)
{
    private val TAG: String = "AppDebug"

    private var job: Job? = null

    fun getAccountProperties(authToken: AuthToken): LiveData<DataState<AccountViewState>> {
        return object: NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(){

            // not used in this case
            override suspend fun createCacheRequestAndReturn() {
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<AccountProperties>) {
                updateLocalDb(response.body)

                withContext(Dispatchers.Main){

                    // finishing by viewing db cache
                    addSourceToResult(loadFromCache(), false)
                }
            }

            override fun cancelOperationIfNoInternetConnection(): Boolean {
                return false
            }

            override fun loadFromCache(): LiveData<AccountViewState> {

                return accountPropertiesDao.searchByPk(authToken.account_pk!!)
                    .switchMap {
                        object: LiveData<AccountViewState>(){
                            override fun onActive() {
                                super.onActive()
                                value = AccountViewState(it)
                            }
                        }
                    }
            }

            override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
                return openApiMainService.getAccountProperties("Token ${authToken.token!!}")
            }

            override suspend fun updateLocalDb(accountProp: AccountProperties?) {
                accountProp?.let {
                    accountPropertiesDao.updateAccountProperties(
                        accountProp.pk,
                        accountProp.email,
                        accountProp.username
                    )
                }
            }

            override fun shouldLoadFromCache(): Boolean {
                return true
            }

            override fun setCurrentJob(job: Job) {
                this@AccountRepository.job?.cancel() // cancel existing jobs
                this@AccountRepository.job = job
            }

            override fun isNetworkRequest(): Boolean {
                return true
            }

        }.asLiveData()
    }

    fun saveAccountProperties(authToken: AuthToken, accountProperties: AccountProperties): LiveData<DataState<AccountViewState>> {
        return object: NetworkBoundResource<GenericResponse, AccountProperties, AccountViewState>(){

            // not used in this case
            override suspend fun createCacheRequestAndReturn() {
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                updateLocalDb(null) // don't care about GenericResponse in local db

                withContext(Dispatchers.Main){
                    // finish with success response
                    onCompleteJob(
                        DataState.data(null,
                            Response(response.body.response, false, true)
                        ))
                }
            }

            override fun shouldLoadFromCache(): Boolean {
                return false // Not loading anything from cache
            }

            // not used in this case
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.saveAccountProperties(
                    "Token ${authToken.token!!}",
                    accountProperties.email,
                    accountProperties.username
                )
            }

            override suspend fun updateLocalDb(cacheObject: AccountProperties?) {
                return accountPropertiesDao.updateAccountProperties(
                    accountProperties.pk,
                    accountProperties.email,
                    accountProperties.username
                )
            }

            override fun setCurrentJob(job: Job) {
                this@AccountRepository.job?.cancel() // cancel existing jobs
                this@AccountRepository.job = job
            }

            override fun cancelOperationIfNoInternetConnection(): Boolean {
                return !sessionManager.isConnectedToTheInternet()
            }

            override fun isNetworkRequest(): Boolean {
                return true
            }

        }.asLiveData()
    }


    fun updatePassword(authToken: AuthToken, currentPassword: String, newPassword: String, confirmNewPassword: String): LiveData<DataState<AccountViewState>> {
        return object: NetworkBoundResource<GenericResponse, Any, AccountViewState>(){

            // not used in this case
            override suspend fun createCacheRequestAndReturn() {
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                withContext(Dispatchers.Main){
                    // finish with success response
                    onCompleteJob(
                        DataState.data(null,
                            Response(response.body.response, false, true)
                        ))
                }
            }

            override fun shouldLoadFromCache(): Boolean {
                return false // Not loading anything from cache
            }

            // not used in this case
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.updatePassword(
                    "Token ${authToken.token!!}",
                    currentPassword,
                    newPassword,
                    confirmNewPassword
                )
            }

            // not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {
            }

            override fun setCurrentJob(job: Job) {
                this@AccountRepository.job?.cancel() // cancel existing jobs
                this@AccountRepository.job = job
            }

            override fun cancelOperationIfNoInternetConnection(): Boolean {
                return !sessionManager.isConnectedToTheInternet()
            }

            override fun isNetworkRequest(): Boolean {
                return true
            }

        }.asLiveData()
    }

    fun cancelRequests(){
        Log.d(TAG, "AccountRepository: cancelling requests... ")
        job?.cancel()
    }
}



















