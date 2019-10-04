package com.codingwithmitch.openapi.repository.main

import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.persistence.AccountPropertiesDao
import com.codingwithmitch.openapi.session.SessionManager
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
)
{

    fun getAccountProperties(authToken: AuthToken): LiveData<DataState<AccountViewState>> {
        return object: NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
            "getAccountProperties",
            sessionManager.isConnectedToTheInternet(),
            true,
            false,
            true
        ){

            // if network is down, view the cache and return
            override suspend fun createCacheRequestAndReturn() {
                withContext(Dispatchers.Main){

                    // finishing by viewing db cache
                    result.addSource(loadFromCache()){ viewState ->
                        onCompleteJob(DataState.data(viewState, null))
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<AccountProperties>) {
                updateLocalDb(response.body)

                withContext(Dispatchers.Main){

                    // finishing by viewing db cache
                    result.addSource(loadFromCache()){ viewState ->
                        onCompleteJob(DataState.data(viewState, null))
                    }
                }
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

            override fun setJob(job: Job) {
                jobManager.addJob(methodName, job)
            }


        }.asLiveData()
    }

}












