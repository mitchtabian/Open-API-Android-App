package com.codingwithmitch.openapi.interactors.blog

import com.codingwithmitch.openapi.business.datasource.cache.blog.toBlogPost
import com.codingwithmitch.openapi.business.datasource.cache.blog.toEntity
import com.codingwithmitch.openapi.business.datasource.network.main.OpenApiMainService
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling.Companion.ERROR_BLOG_DOES_NOT_EXIST
import com.codingwithmitch.openapi.business.domain.util.MessageType
import com.codingwithmitch.openapi.business.domain.util.SuccessHandling
import com.codingwithmitch.openapi.business.domain.util.UIComponentType
import com.codingwithmitch.openapi.business.interactors.blog.ConfirmBlogExistsOnServer
import com.codingwithmitch.openapi.datasource.cache.AppDatabaseFake
import com.codingwithmitch.openapi.datasource.cache.BlogDaoFake
import com.codingwithmitch.openapi.datasource.network.blog.ConfirmBlogExistsOnServerResponses
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
 * 1. Success (The blog exists in the cache and on the server)
 * 2. Success (The blog does not exist in the cache)
 * 3. Failure (AuthToken is null)
 * 4. Failure (Cache: Yes, Server: DNE) - server returns 404
 */
class ConfirmBlogExistsOnServerTest {

    private val appDatabase = AppDatabaseFake()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var baseUrl: HttpUrl

    // system in test
    private lateinit var confirmBlogExistsOnServer: ConfirmBlogExistsOnServer

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
        confirmBlogExistsOnServer = ConfirmBlogExistsOnServer(
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
        val cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog?.toBlogPost() == blogPost)

        // Execute the use case
        val emissions = confirmBlogExistsOnServer.execute(
            authToken = authToken,
            pk = blogPost.pk,
            slug = blogPost.slug,
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is a success message
        assert(emissions[1].data?.message == SuccessHandling.SUCCESS_BLOG_EXISTS_ON_SERVER)
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
        val cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog == null)

        // Execute the use case
        val emissions = confirmBlogExistsOnServer.execute(
            authToken = authToken,
            pk = blogPost.pk,
            slug = blogPost.slug,
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is a success message
        assert(emissions[1].data?.message == SuccessHandling.SUCCESS_BLOG_DOES_NOT_EXIST_IN_CACHE)
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
        val cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog?.toBlogPost() == blogPost)

        // Execute the use case
        val emissions = confirmBlogExistsOnServer.execute(
            authToken = authToken,
            pk = blogPost.pk,
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
        var cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog?.toBlogPost() == blogPost)

        // Execute the use case
        val emissions = confirmBlogExistsOnServer.execute(
            authToken = authToken,
            pk = blogPost.pk,
            slug = blogPost.slug,
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // Confirm it was removed from the cache
        cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog == null)

        // confirm second emission is an error dialog
        assert(emissions[1].stateMessage?.response?.message == ERROR_BLOG_DOES_NOT_EXIST)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

}














