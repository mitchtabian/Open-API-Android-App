package com.codingwithmitch.openapi.repository.auth

import com.codingwithmitch.openapi.api.auth.OpenApiAuthService
import com.codingwithmitch.openapi.persistence.AccountPropertiesDao
import com.codingwithmitch.openapi.persistence.AuthTokenDao
import com.codingwithmitch.openapi.session.SessionManager

class AuthRepository
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
)
{

}