package com.templateapp.cloudapi.interactors.task

import com.templateapp.cloudapi.business.datasource.cache.task.toTask
import com.templateapp.cloudapi.business.datasource.cache.task.toEntity
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_TASK_DOES_NOT_EXIST
import com.templateapp.cloudapi.business.domain.util.MessageType
import com.templateapp.cloudapi.business.domain.util.SuccessHandling
import com.templateapp.cloudapi.business.domain.util.UIComponentType
import com.templateapp.cloudapi.business.interactors.task.ConfirmTaskExistsOnServer
import com.templateapp.cloudapi.datasource.cache.AppDatabaseFake
import com.templateapp.cloudapi.datasource.cache.BlogDaoFake
import com.templateapp.cloudapi.datasource.network.task.ConfirmBlogExistsOnServerResponses
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

/**
 * 1. Success (The task exists in the cache and on the server)
 * 2. Success (The task does not exist in the cache)
 * 3. Failure (AuthToken is null)
 * 4. Failure (Cache: Yes, Server: DNE) - server returns 404
 */
class ConfirmTaskExistsOnServerTest {

    private val appDatabase = AppDatabaseFake()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var baseUrl: HttpUrl

    // system in test
    private lateinit var confirmTaskExistsOnServer: ConfirmTaskExistsOnServer

    // dependencies
    private lateinit var service: OpenApiMainService
    private lateinit var cache: BlogDaoFake

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        baseUrl = mockWebServer.url("/api/recipe/")
        service = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(OpenApiMainService::class.java)

        cache = BlogDaoFake(appDatabase)

        // instantiate the system in test
        confirmTaskExistsOnServer = ConfirmTaskExistsOnServer(
            service = service,
            cache = cache,
        )
    }

    @Test
    fun success_blogExists() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(ConfirmBlogExistsOnServerResponses.success_blogExists)
        )

        // User Information
        val authToken = ConfirmBlogExistsOnServerResponses.authToken

        // Blog
        val blogPost = ConfirmBlogExistsOnServerResponses.blogPost

        // Insert the blog into the cache
        cache.insert(blogPost.toEntity())

        // Confirm the blog exists in the cache
        val cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog?.toTask() == blogPost)

        // Execute the use case
        val emissions = confirmTaskExistsOnServer.execute(
            authToken = authToken,
            id = blogPost.id,
            slug = blogPost.slug,
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is a success message
        assert(emissions[1].data?.message == SuccessHandling.SUCCESS_TASK_EXISTS_ON_SERVER)
        assert(emissions[1].data?.uiComponentType is UIComponentType.None)
        assert(emissions[1].data?.messageType is MessageType.Success)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun success_blogDoesNotExistInCache() = runBlocking {

        // User Information
        val authToken = ConfirmBlogExistsOnServerResponses.authToken

        // Blog
        val blogPost = ConfirmBlogExistsOnServerResponses.blogPost

        // Confirm the blog does NOT exist in the cache
        val cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog == null)

        // Execute the use case
        val emissions = confirmTaskExistsOnServer.execute(
            authToken = authToken,
            id = blogPost.id,
            slug = blogPost.slug,
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is a success message
        assert(emissions[1].data?.message == SuccessHandling.SUCCESS_TASK_DOES_NOT_EXIST_IN_CACHE)
        assert(emissions[1].data?.uiComponentType is UIComponentType.None)
        assert(emissions[1].data?.messageType is MessageType.Success)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun fail_authTokenNull() = runBlocking {

        // User Information
        val authToken = null

        // Blog
        val blogPost = ConfirmBlogExistsOnServerResponses.blogPost

        // Insert the blog into the cache
        cache.insert(blogPost.toEntity())

        // Confirm the blog exists in the cache
        val cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog?.toTask() == blogPost)

        // Execute the use case
        val emissions = confirmTaskExistsOnServer.execute(
            authToken = authToken,
            id = blogPost.id,
            slug = blogPost.slug,
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is an error dialog
        assert(emissions[1].stateMessage?.response?.message == ERROR_AUTH_TOKEN_INVALID)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    /**
     * The blogpost exists in the cache but does not exist on the server.
     */
    @Test
    fun fail_blogDoesNotExistOnServer() = runBlocking {

        // User Information
        val authToken = ConfirmBlogExistsOnServerResponses.authToken

        // Blog
        val blogPost = ConfirmBlogExistsOnServerResponses.blogPost

        // Insert the blog into the cache
        cache.insert(blogPost.toEntity())

        // Confirm the blog exists in the cache
        var cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog?.toTask() == blogPost)

        // Execute the use case
        val emissions = confirmTaskExistsOnServer.execute(
            authToken = authToken,
            id = blogPost.id,
            slug = blogPost.slug,
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // Confirm it was removed from the cache
        cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog == null)

        // confirm second emission is an error dialog
        assert(emissions[1].stateMessage?.response?.message == ERROR_TASK_DOES_NOT_EXIST)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

}














