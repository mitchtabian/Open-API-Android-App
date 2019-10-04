package com.codingwithmitch.openapi.repository.main

import android.util.Log
import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.persistence.AccountPropertiesDao
import com.codingwithmitch.openapi.session.SessionManager
import kotlinx.coroutines.Job
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

    private var repositoryJob: Job? = null


    fun cancelActiveJobs(){
        Log.d(TAG, "AuthRepository: Cancelling on-going jobs...")
        repositoryJob?.cancel()
    }
}












