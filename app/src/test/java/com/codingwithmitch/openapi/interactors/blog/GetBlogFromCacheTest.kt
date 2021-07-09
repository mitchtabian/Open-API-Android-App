package com.codingwithmitch.openapi.interactors.blog

import com.codingwithmitch.openapi.business.datasource.cache.blog.BlogPostDao
import com.codingwithmitch.openapi.business.datasource.cache.blog.toBlogPost
import com.codingwithmitch.openapi.business.datasource.cache.blog.toEntity
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling
import com.codingwithmitch.openapi.business.domain.util.MessageType
import com.codingwithmitch.openapi.business.domain.util.UIComponentType
import com.codingwithmitch.openapi.business.interactors.blog.GetBlogFromCache
import com.codingwithmitch.openapi.datasource.cache.AppDatabaseFake
import com.codingwithmitch.openapi.datasource.cache.BlogDaoFake
import com.codingwithmitch.openapi.datasource.network.blog.GetBlogFromCacheResponses
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * 1. Get success
 * 2. Get failure (Does not exist in cache)
 */
class GetBlogFromCacheTest {

    private val appDatabase = AppDatabaseFake()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var baseUrl: HttpUrl

    // system in test
    private lateinit var getBlogFromCache: GetBlogFromCache

    // dependencies
    private lateinit var cache: BlogPostDao

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        baseUrl = mockWebServer.url("/api/recipe/")

        cache = BlogDaoFake(appDatabase)

        // instantiate the system in test
        getBlogFromCache = GetBlogFromCache(
            cache = cache,
        )
    }

    @Test
    fun getBlogSuccess() = runBlocking {
        // Blog
        val blogPost = GetBlogFromCacheResponses.blogPost

        // Make sure the blog is in the cache
        cache.insert(blogPost.toEntity())

        // Confirm the blog is in the cache
        val cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog?.toBlogPost() == blogPost)

        // execute use case
        val emissions = getBlogFromCache.execute(blogPost.pk).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is the cached BlogPost
        assert(emissions[1].data == blogPost)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun getBlogFail_doesNotExistInCache() = runBlocking {
        val blogPost = GetBlogFromCacheResponses.blogPost

        // Confirm the blog is not in the cache
        val cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog == null)

        // execute use case
        val emissions = getBlogFromCache.execute(blogPost.pk).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is an error dialog
        assert(emissions[1].data == null)
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_BLOG_UNABLE_TO_RETRIEVE)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)


        // loading done
        assert(!emissions[1].isLoading)
    }
}














