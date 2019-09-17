package com.codingwithmitch.openapi.session

import android.app.Application
import com.codingwithmitch.openapi.persistence.AuthTokenDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
)
{

}