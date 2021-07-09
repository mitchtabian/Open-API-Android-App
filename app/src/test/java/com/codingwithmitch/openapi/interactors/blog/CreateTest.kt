package com.codingwithmitch.openapi.interactors.blog

import com.codingwithmitch.openapi.business.datasource.cache.blog.BlogPostDao
import com.codingwithmitch.openapi.business.datasource.cache.blog.toBlogPost
import com.codingwithmitch.openapi.business.datasource.network.main.OpenApiMainService
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling.Companion.ERROR_BLOG_BODY_LENGTH
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling.Companion.ERROR_BLOG_IMAGE_ASPECT_RATIO
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling.Companion.ERROR_BLOG_IMAGE_SIZE
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling.Companion.ERROR_BLOG_TITLE_LENGTH
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling.Companion.ERROR_NOT_CWM_MEMBER
import com.codingwithmitch.openapi.business.domain.util.MessageType
import com.codingwithmitch.openapi.business.domain.util.SuccessHandling
import com.codingwithmitch.openapi.business.domain.util.UIComponentType
import com.codingwithmitch.openapi.business.interactors.blog.PublishBlog
import com.codingwithmitch.openapi.datasource.cache.AppDatabaseFake
import com.codingwithmitch.openapi.datasource.cache.BlogDaoFake
import com.codingwithmitch.openapi.datasource.network.blog.CreateResponses
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

/**
 * 1. Create success
 * 2. Delete failure (Title must be 5+ chars)
 * 3. Delete failure (Body must be 50+ chars)
 * 4. Delete failure (Image too large)
 * 5. Delete failure (Image aspect ratio incorrect)
 */
class CreateTest {

    private val appDatabase = AppDatabaseFake()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var baseUrl: HttpUrl

    // system in test
    private lateinit var publishBlog: PublishBlog

    // dependencies
    private lateinit var service: OpenApiMainService
    private lateinit var cache: BlogPostDao

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
        publishBlog = PublishBlog(
            service = service,
            cache = cache,
        )
    }

    @Test
    fun createSuccess() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(CreateResponses.createSuccess)
        )

        // User Information
        val authToken = CreateResponses.authToken

        // Blog
        val blogPost = CreateResponses.blogPost

        // Confirm the blog does not exist in the cache
        var cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog == null)

        // publish the blog
        val title = blogPost.title.toRequestBody("text/plain".toMediaTypeOrNull())
        val body = blogPost.body.toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartBody: MultipartBody.Part? = null // can be null since just a test
        val emissions = publishBlog.execute(
            authToken = authToken,
            title = title,
            body = body,
            image = multipartBody
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm it was inserted into the cache
        cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog?.toBlogPost() == blogPost)

        // confirm second emission is a success message
        assert(emissions[1].data?.message == SuccessHandling.SUCCESS_BLOG_CREATED)
        assert(emissions[1].data?.uiComponentType is UIComponentType.None)
        assert(emissions[1].data?.messageType is MessageType.Success)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun createFail_titleMustBe5Chars() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(CreateResponses.createFail_titleMustBe5Chars)
        )

        // User Information
        val authToken = CreateResponses.authToken

        // Blog
        val blogPost = CreateResponses.blogPost

        // Confirm the blog does not exist in the cache
        var cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog == null)

        // Attempt to publish the blog
        val title = blogPost.title.toRequestBody("text/plain".toMediaTypeOrNull())
        val body = blogPost.body.toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartBody: MultipartBody.Part? = null // can be null since just a test
        val emissions = publishBlog.execute(
            authToken = authToken,
            title = title,
            body = body,
            image = multipartBody
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm it was NOT inserted into the cache
        cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog == null)

        // confirm second emission is an error dialog
        assert(emissions[1].stateMessage?.response?.message == ERROR_BLOG_TITLE_LENGTH)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun createFail_bodyMustBe50Chars() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(CreateResponses.createFail_bodyMustBe50Chars)
        )

        // User Information
        val authToken = CreateResponses.authToken

        // Blog
        val blogPost = CreateResponses.blogPost

        // Confirm the blog does not exist in the cache
        var cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog == null)

        // Attempt to publish the blog
        val title = blogPost.title.toRequestBody("text/plain".toMediaTypeOrNull())
        val body = blogPost.body.toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartBody: MultipartBody.Part? = null // can be null since just a test
        val emissions = publishBlog.execute(
            authToken = authToken,
            title = title,
            body = body,
            image = multipartBody
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm it was NOT inserted into the cache
        cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog == null)

        // confirm second emission is an error dialog
        assert(emissions[1].stateMessage?.response?.message == ERROR_BLOG_BODY_LENGTH)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun createFail_imageTooLarge() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(CreateResponses.createFail_imageTooLarge)
        )

        // User Information
        val authToken = CreateResponses.authToken

        // Blog
        val blogPost = CreateResponses.blogPost

        // Confirm the blog does not exist in the cache
        var cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog == null)

        // Attempt to publish the blog
        val title = blogPost.title.toRequestBody("text/plain".toMediaTypeOrNull())
        val body = blogPost.body.toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartBody: MultipartBody.Part? = null // can be null since just a test
        val emissions = publishBlog.execute(
            authToken = authToken,
            title = title,
            body = body,
            image = multipartBody
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm it was NOT inserted into the cache
        cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog == null)

        // confirm second emission is an error dialog
        assert(emissions[1].stateMessage?.response?.message == ERROR_BLOG_IMAGE_SIZE)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun createFail_imageAspectRatio() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(CreateResponses.createFail_imageAspectRatio)
        )

        // User Information
        val authToken = CreateResponses.authToken

        // Blog
        val blogPost = CreateResponses.blogPost

        // Confirm the blog does not exist in the cache
        var cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog == null)

        // Attempt to publish the blog
        val title = blogPost.title.toRequestBody("text/plain".toMediaTypeOrNull())
        val body = blogPost.body.toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartBody: MultipartBody.Part? = null // can be null since just a test
        val emissions = publishBlog.execute(
            authToken = authToken,
            title = title,
            body = body,
            image = multipartBody
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm it was NOT inserted into the cache
        cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog == null)

        // confirm second emission is an error dialog
        assert(emissions[1].stateMessage?.response?.message == ERROR_BLOG_IMAGE_ASPECT_RATIO)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun createFail_notCwmMember() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(CreateResponses.createFail_notCwmMember)
        )

        // User Information
        val authToken = CreateResponses.authToken

        // Blog
        val blogPost = CreateResponses.blogPost

        // Confirm the blog does not exist in the cache
        var cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog == null)

        // Attempt to publish the blog
        val title = blogPost.title.toRequestBody("text/plain".toMediaTypeOrNull())
        val body = blogPost.body.toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartBody: MultipartBody.Part? = null // can be null since just a test
        val emissions = publishBlog.execute(
            authToken = authToken,
            title = title,
            body = body,
            image = multipartBody
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm it was NOT inserted into the cache
        cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog == null)

        // confirm second emission is an error dialog
        assert(emissions[1].stateMessage?.response?.message == ERROR_NOT_CWM_MEMBER)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }
}













