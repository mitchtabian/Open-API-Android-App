package com.templateapp.cloudapi.di.auth

import com.templateapp.cloudapi.business.datasource.cache.account.AccountDao
import com.templateapp.cloudapi.business.datasource.cache.auth.AuthTokenDao
import com.templateapp.cloudapi.business.datasource.datastore.AppDataStore
import com.templateapp.cloudapi.business.datasource.network.auth.OpenApiAuthService
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.interactors.account.GetAllUsers
import com.templateapp.cloudapi.business.interactors.auth.Login
import com.templateapp.cloudapi.business.interactors.auth.Register
import com.templateapp.cloudapi.business.interactors.session.CheckPreviousAuthUser
import com.templateapp.cloudapi.business.interactors.session.Logout
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
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
        appDataStoreManager: AppDataStore,
        serverMsgTranslator: ServerMsgTranslator
    ): Login {
        return Login(
            service,
            accountDao,
            authTokenDao,
            appDataStoreManager,
            serverMsgTranslator
        )
    }

    @Singleton
    @Provides
    fun provideLogout(
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
        appDataStoreManager: AppDataStore,
        serverMsgTranslator: ServerMsgTranslator
    ): Register {
        return Register(
            service,
            accountDao,
            authTokenDao,
            appDataStoreManager,
            serverMsgTranslator
        )
    }

}









