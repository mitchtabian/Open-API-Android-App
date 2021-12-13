package com.templateapp.cloudapi.di.task

import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.datasource.datastore.AppDataStore
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.interactors.task.*
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
        dao: TaskDao
    ): GetTaskFromCache{
        return GetTaskFromCache(dao)
    }

    @Singleton
    @Provides
    fun provideIsOwnerOfTask(
        service: OpenApiMainService
    ): IsOwnerOfTask{
        return IsOwnerOfTask(service)
    }

    @Singleton
    @Provides
    fun provideSearchTasks(
        service: OpenApiMainService,
        dao: TaskDao,
    ): SearchTasks{
        return SearchTasks(service, dao)
    }

    @Singleton
    @Provides
    fun provideDeleteTask(
        service: OpenApiMainService,
        dao: TaskDao,
    ): DeleteTask{
        return DeleteTask(service, dao)
    }

    @Singleton
    @Provides
    fun provideUpdateTask(
        service: OpenApiMainService,
        dao: TaskDao,
    ): UpdateTask{
        return UpdateTask(service, dao)
    }

    @Singleton
    @Provides
    fun providePublishTask(
        service: OpenApiMainService,
        dao: TaskDao,
    ): PublishTask{
        return PublishTask(service, dao)
    }

    @Singleton
    @Provides
    fun provideGetOrderAndFilter(
        appDataStoreManager: AppDataStore
    ): GetOrderAndFilter{
        return GetOrderAndFilter(appDataStoreManager)
    }

    @Singleton
    @Provides
    fun provideConfirmTaskExistsOnServer(
        service: OpenApiMainService,
        cache: TaskDao,
    ): ConfirmTaskExistsOnServer{
        return ConfirmTaskExistsOnServer(service = service, cache = cache)
    }
}

















