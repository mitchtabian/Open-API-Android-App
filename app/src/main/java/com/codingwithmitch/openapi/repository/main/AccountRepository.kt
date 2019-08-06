package com.codingwithmitch.openapi.repository.main

import androidx.lifecycle.LiveData
import com.codingwithmitch.openapi.api.GenericApiResponse
import com.codingwithmitch.openapi.api.GenericResponse
import com.codingwithmitch.openapi.api.main.AccountNetworkBoundResource
import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.persistence.AccountPropertiesDao
import com.codingwithmitch.openapi.ui.main.account.state.AccountDataState
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao
)
{
    private val TAG: String = "AppDebug"


    fun getAccountProperties(authToken: AuthToken): LiveData<AccountDataState> {
       return object: AccountNetworkBoundResource<AccountProperties>(){

           override fun loadFromDb(): LiveData<AccountProperties> {
               return accountPropertiesDao.searchByPk(authToken.account_pk!!)
           }

           override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
               return openApiMainService.getAccountProperties("Token ${authToken.token!!}")
           }

           override fun updateLocalDb() {
               // empty
           }

           override fun saveToLocalDb(accountProperties: AccountProperties) {
               accountPropertiesDao.insertAndReplace(accountProperties)
           }

           override fun isGetRequest(): Boolean {
               return true
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

            override fun updateLocalDb() {
                accountPropertiesDao.updateAccountProperties(
                    accountProperties.email,
                    accountProperties.username,
                    accountProperties.pk
                )
            }

            override fun saveToLocalDb(accountProperties: AccountProperties) {
                // empty
            }

            override fun isGetRequest(): Boolean {
                return false
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

            override fun updateLocalDb() {
                // ignore
            }

            override fun saveToLocalDb(accountProperties: AccountProperties) {
                // empty
            }

            override fun isGetRequest(): Boolean {
                return false
            }
        }.asLiveData()
    }

    private fun extractErrorMessage(e: String?): String{
        var msg = e
        if(msg == null){
            msg = "Unknown Error"
        }
        return msg
    }


}



















