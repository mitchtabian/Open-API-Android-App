package com.codingwithmitch.openapi.di.auth

import com.codingwithmitch.openapi.api.auth.OpenApiAuthService
import com.codingwithmitch.openapi.interactors.auth.Login
import com.codingwithmitch.openapi.interactors.auth.Register
import com.codingwithmitch.openapi.interactors.session.CheckPreviousAuthUser
import com.codingwithmitch.openapi.interactors.session.Logout
import com.codingwithmitch.openapi.persistence.account.AccountDao
import com.codingwithmitch.openapi.persistence.auth.AuthTokenDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.FlowPreview
import retrofit2.Retrofit
import javax.inject.Singleton

@FlowPreview
@Module
@InstallIn(SingletonComponent::class)
object AuthModule{

    @Singleton
    @Provides
    fun provideOpenApiAuthService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder
            .build()
            .create(OpenApiAuthService::class.java)
    }

    @Singleton
    @Provides
    fun provideCheckPrevAuthUser(
        accountDao: AccountDao,
        authTokenDao: AuthTokenDao,
    ): CheckPreviousAuthUser {
        return CheckPreviousAuthUser(
            accountDao,
            authTokenDao
        )
    }

    @Singleton
    @Provides
    fun provideLogin(
        service: OpenApiAuthService,
        accountDao: AccountDao,
        authTokenDao: AuthTokenDao,
    ): Login {
        return Login(
            service,
            accountDao,
            authTokenDao
        )
    }

    @Singleton
    @Provides
    fun provideLogout(
        service: OpenApiAuthService,
        accountDao: AccountDao,
        authTokenDao: AuthTokenDao,
    ): Logout {
        return Logout(authTokenDao)
    }

    @Singleton
    @Provides
    fun provideRegister(
        service: OpenApiAuthService,
        accountDao: AccountDao,
        authTokenDao: AuthTokenDao,
    ): Register {
        return Register(
            service,
            accountDao,
            authTokenDao
        )
    }
}









