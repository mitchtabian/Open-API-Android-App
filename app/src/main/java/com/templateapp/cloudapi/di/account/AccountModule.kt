package com.templateapp.cloudapi.di.account

import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.datasource.cache.account.AccountDao
import com.templateapp.cloudapi.business.datasource.cache.account.RoleDao
import com.templateapp.cloudapi.business.datasource.cache.auth.AuthTokenDao
import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.interactors.account.*
import com.templateapp.cloudapi.business.interactors.task.SearchTasks
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AccountModule {

    @Singleton
    @Provides
    fun provideGetAccount(
        service: OpenApiMainService,
        accountCache: AccountDao,
        tokenCache: AuthTokenDao,
        serverMsgTranslator: ServerMsgTranslator
    ): GetAccount{
        return GetAccount(service, accountCache, tokenCache, serverMsgTranslator)
    }

    @Singleton
    @Provides
    fun provideUpdateAccount(
        service: OpenApiMainService,
        cache: AccountDao,
        serverMsgTranslator: ServerMsgTranslator
    ): UpdateAccount{
        return UpdateAccount(service, cache, serverMsgTranslator)
    }

    @Singleton
    @Provides
    fun provideChangeAccount(
        service: OpenApiMainService,
        cache: AccountDao,
        serverMsgTranslator: ServerMsgTranslator
    ): ChangeAccount{
        return ChangeAccount(service, cache, serverMsgTranslator)
    }

    @Singleton
    @Provides
    fun provideGetAccountFromCache(
        cache: AccountDao,
        serverMsgTranslator: ServerMsgTranslator
    ): GetAccountFromCache{
        return GetAccountFromCache(cache, serverMsgTranslator)
    }

    @Singleton
    @Provides
    fun provideUpdatePassword(
        service: OpenApiMainService,
        cache: AccountDao,
        serverMsgTranslator: ServerMsgTranslator
    ): UpdatePassword{
        return UpdatePassword(service, serverMsgTranslator)
    }

    @Singleton
    @Provides
    fun provideAllUsers(
        service: OpenApiMainService,
        cache: AccountDao,
        serverMsgTranslator: ServerMsgTranslator
    ): GetAllUsers {
        return GetAllUsers(service, cache, serverMsgTranslator)
    }

    @Singleton
    @Provides
    fun provideAllRoles(
        service: OpenApiMainService,
        cache: RoleDao,
        serverMsgTranslator: ServerMsgTranslator
    ): GetAllRoles {
        return GetAllRoles(service, cache, serverMsgTranslator)
    }
}










