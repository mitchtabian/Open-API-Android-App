package com.templateapp.cloudapi.interactors.task

import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.datasource.cache.task.toTask
import com.templateapp.cloudapi.business.datasource.cache.task.toEntity
import com.templateapp.cloudapi.business.domain.util.ErrorHandling
import com.templateapp.cloudapi.business.domain.util.MessageType
import com.templateapp.cloudapi.business.domain.util.UIComponentType
import com.templateapp.cloudapi.business.interactors.task.GetTaskFromCache
import com.templateapp.cloudapi.datasource.cache.AppDatabaseFake
import com.templateapp.cloudapi.datasource.cache.BlogDaoFake
import com.templateapp.cloudapi.datasource.network.task.GetBlogFromCacheResponses
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
class GetTaskFromCacheTest {

    private val appDatabase = AppDatabaseFake()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var baseUrl: HttpUrl

    // system in test
    private lateinit var getTaskFromCache: GetTaskFromCache

    // dependencies
    private lateinit var cache: TaskDao

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        baseUrl = mockWebServer.url("/api/recipe/")

        cache = BlogDaoFake(appDatabase)

        // instantiate the system in test
        getTaskFromCache = GetTaskFromCache(
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
        val cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog?.toTask() == blogPost)

        // execute use case
        val emissions = getTaskFromCache.execute(blogPost.id).toList()

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
        val cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog == null)

        // execute use case
        val emissions = getTaskFromCache.execute(blogPost.id).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is an error dialog
        assert(emissions[1].data == null)
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_TASK_UNABLE_TO_RETRIEVE)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)


        // loading done
        assert(!emissions[1].isLoading)
    }
}














