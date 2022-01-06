package com.templateapp.cloudapi.di.task

import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.datasource.datastore.AppDataStore
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.interactors.task.*
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaskModule {

    @Singleton
    @Provides
    fun provideGetTaskFromCache(
        dao: TaskDao,
        serverMsgTranslator: ServerMsgTranslator
    ): GetTaskFromCache{
        return GetTaskFromCache(dao, serverMsgTranslator)
    }

    @Singleton
    @Provides
    fun provideIsOwnerOfTask(
        service: OpenApiMainService,
        serverMsgTranslator: ServerMsgTranslator
    ): IsOwnerOfTask{
        return IsOwnerOfTask(service, serverMsgTranslator)
    }

    @Singleton
    @Provides
    fun provideSearchTasks(
        service: OpenApiMainService,
        dao: TaskDao,
        serverMsgTranslator: ServerMsgTranslator
    ): SearchTasks{
        return SearchTasks(service, dao, serverMsgTranslator)
    }

    @Singleton
    @Provides
    fun provideDeleteTask(
        service: OpenApiMainService,
        dao: TaskDao,
        serverMsgTranslator: ServerMsgTranslator
    ): DeleteTask{
        return DeleteTask(service, dao, serverMsgTranslator)
    }

    @Singleton
    @Provides
    fun provideUpdateTask(
        service: OpenApiMainService,
        dao: TaskDao,
        serverMsgTranslator: ServerMsgTranslator
    ): UpdateTask{
        return UpdateTask(service, dao, serverMsgTranslator)
    }

    @Singleton
    @Provides
    fun providePublishTask(
        service: OpenApiMainService,
        dao: TaskDao,
        serverMsgTranslator: ServerMsgTranslator
    ): PublishTask{
        return PublishTask(service, dao, serverMsgTranslator)
    }

    @Singleton
    @Provides
    fun provideGetOrderAndFilter(
        appDataStoreManager: AppDataStore,
        serverMsgTranslator: ServerMsgTranslator
    ): GetOrderAndFilter{
        return GetOrderAndFilter(appDataStoreManager, serverMsgTranslator)
    }

    @Singleton
    @Provides
    fun provideConfirmTaskExistsOnServer(
        service: OpenApiMainService,
        cache: TaskDao,
        serverMsgTranslator: ServerMsgTranslator
    ): ConfirmTaskExistsOnServer{
        return ConfirmTaskExistsOnServer(service = service, cache = cache, serverMsgTranslator = serverMsgTranslator)
    }
}

















