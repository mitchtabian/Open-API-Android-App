package com.codingwithmitch.openapi.di.account

import com.codingwithmitch.openapi.business.datasource.network.main.OpenApiMainService
import com.codingwithmitch.openapi.business.interactors.account.GetAccount
import com.codingwithmitch.openapi.business.interactors.account.GetAccountFromCache
import com.codingwithmitch.openapi.business.interactors.account.UpdateAccount
import com.codingwithmitch.openapi.business.interactors.account.UpdatePassword
import com.codingwithmitch.openapi.business.datasource.cache.account.AccountDao
import com.codingwithmitch.openapi.business.datasource.cache.auth.AuthTokenDao
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
    ): GetAccount{
        return GetAccount(service, accountCache, tokenCache)
    }

    @Singleton
    @Provides
    fun provideUpdateAccount(
        service: OpenApiMainService,
        cache: AccountDao,
    ): UpdateAccount{
        return UpdateAccount(service, cache)
    }

    @Singleton
    @Provides
    fun provideGetAccountFromCache(
        cache: AccountDao,
    ): GetAccountFromCache{
        return GetAccountFromCache(cache)
    }

    @Singleton
    @Provides
    fun provideUpdatePassword(
        service: OpenApiMainService,
        cache: AccountDao,
    ): UpdatePassword{
        return UpdatePassword(service)
    }
}










