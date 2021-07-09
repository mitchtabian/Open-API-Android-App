package com.codingwithmitch.openapi.di.blog

import com.codingwithmitch.openapi.business.datasource.cache.blog.BlogPostDao
import com.codingwithmitch.openapi.business.datasource.datastore.AppDataStore
import com.codingwithmitch.openapi.business.datasource.network.main.OpenApiMainService
import com.codingwithmitch.openapi.business.interactors.blog.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BlogModule {

    @Singleton
    @Provides
    fun provideGetBlogFromCache(
        dao: BlogPostDao
    ): GetBlogFromCache{
        return GetBlogFromCache(dao)
    }

    @Singleton
    @Provides
    fun provideIsAuthorOfBlogPost(
        service: OpenApiMainService
    ): IsAuthorOfBlogPost{
        return IsAuthorOfBlogPost(service)
    }

    @Singleton
    @Provides
    fun provideSearchBlogs(
        service: OpenApiMainService,
        dao: BlogPostDao,
    ): SearchBlogs{
        return SearchBlogs(service, dao)
    }

    @Singleton
    @Provides
    fun provideDeleteBlog(
        service: OpenApiMainService,
        dao: BlogPostDao,
    ): DeleteBlogPost{
        return DeleteBlogPost(service, dao)
    }

    @Singleton
    @Provides
    fun provideUpdateBlog(
        service: OpenApiMainService,
        dao: BlogPostDao,
    ): UpdateBlogPost{
        return UpdateBlogPost(service, dao)
    }

    @Singleton
    @Provides
    fun providePublishBlog(
        service: OpenApiMainService,
        dao: BlogPostDao,
    ): PublishBlog{
        return PublishBlog(service, dao)
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
    fun provideConfirmBlogExistsOnServer(
        service: OpenApiMainService,
        cache: BlogPostDao,
    ): ConfirmBlogExistsOnServer{
        return ConfirmBlogExistsOnServer(service = service, cache = cache)
    }
}

















