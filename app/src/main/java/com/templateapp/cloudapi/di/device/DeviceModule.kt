package com.templateapp.cloudapi.di.device

import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.datasource.cache.account.AccountDao
import com.templateapp.cloudapi.business.datasource.cache.account.RoleDao
import com.templateapp.cloudapi.business.datasource.cache.auth.AuthTokenDao
import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.interactors.account.*
import com.templateapp.cloudapi.business.interactors.auth.GetDevice
import com.templateapp.cloudapi.business.interactors.task.SearchTasks
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DeviceModule {

    @Singleton
    @Provides
    fun provideGetDevices(
        service: OpenApiMainService,
        serverMsgTranslator: ServerMsgTranslator
    ): GetDevice{
        return GetDevice(service, serverMsgTranslator)
    }

}










