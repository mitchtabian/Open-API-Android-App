package com.codingwithmitch.openapi.di.blog

import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.interactors.blog.DeleteBlogPost
import com.codingwithmitch.openapi.interactors.blog.GetBlogFromCache
import com.codingwithmitch.openapi.interactors.blog.IsAuthorOfBlogPost
import com.codingwithmitch.openapi.interactors.blog.SearchBlogs
import com.codingwithmitch.openapi.persistence.blog.BlogPostDao
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
}

















