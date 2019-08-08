package com.codingwithmitch.openapi.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import com.codingwithmitch.openapi.api.GenericApiResponse
import com.codingwithmitch.openapi.api.GenericResponse
import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.persistence.AccountPropertiesDao
import com.codingwithmitch.openapi.ui.main.account.state.AccountDataState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.cancel
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao
)
{
    private val TAG: String = "AppDebug"

    val repositoryCoroutineScope: CoroutineScope = CoroutineScope(IO)


    fun getAccountProperties(authToken: AuthToken): LiveData<AccountDataState> {
       return object: AccountNetworkBoundResource<AccountProperties>(){

           override fun loadFromDb(): LiveData<AccountProperties> {
               return accountPropertiesDao.searchByPk(authToken.account_pk!!)
           }

           override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
               return openApiMainService.getAccountProperties("Token ${authToken.token!!}")
           }

           override fun updateLocalDb(accountProp: AccountProperties?) {
               accountProp?.let {
                   accountPropertiesDao.updateAccountProperties(
                       accountProp.pk,
                       accountProp.email,
                       accountProp.username
                   )
               }
           }

           override fun isGetRequest(): Boolean {
               return true
           }

           override fun getCoroutineScope(): CoroutineScope {
               return repositoryCoroutineScope
           }
       }.asLiveData()
    }

    fun saveAccountProperties(authToken: AuthToken, accountProperties: AccountProperties): LiveData<AccountDataState> {
        return object: AccountNetworkBoundResource<GenericResponse>(){

            override fun loadFromDb(): LiveData<AccountProperties> {
                return accountPropertiesDao.searchByPk(accountProperties.pk)
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.saveAccountProperties(
                    "Token ${authToken.token!!}",
                    accountProperties.email,
                    accountProperties.username
                )
            }

            override fun updateLocalDb(accountProp: AccountProperties?) {
                // @accountProp will be null here
                accountPropertiesDao.updateAccountProperties(
                    accountProperties.pk,
                    accountProperties.email,
                    accountProperties.username
                )
            }

            override fun isGetRequest(): Boolean {
                return false
            }

            override fun getCoroutineScope(): CoroutineScope {
                return repositoryCoroutineScope
            }
        }.asLiveData()
    }


    fun updatePassword(authToken: AuthToken, currentPassword: String, newPassword: String, confirmNewPassword: String): LiveData<AccountDataState> {
        return object: AccountNetworkBoundResource<GenericResponse>(){

            override fun loadFromDb(): LiveData<AccountProperties> {
                // ignore. This will not get executed
                return accountPropertiesDao.searchByPk(-1)
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.updatePassword(
                    "Token ${authToken.token!!}",
                    currentPassword,
                    newPassword,
                    confirmNewPassword
                )
            }

            override fun updateLocalDb(accountProp: AccountProperties?) {
                // ignore
            }


            override fun isGetRequest(): Boolean {
                return false
            }

            override fun getCoroutineScope(): CoroutineScope {
                return repositoryCoroutineScope
            }
        }.asLiveData()
    }

    fun cancelRequests(){
        Log.d(TAG, "cancelling requests...: ")
        repositoryCoroutineScope.cancel()
    }
}


















