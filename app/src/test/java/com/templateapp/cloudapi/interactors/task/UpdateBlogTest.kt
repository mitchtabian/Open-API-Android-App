package com.templateapp.cloudapi.interactors.task

import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.datasource.cache.task.toTask
import com.templateapp.cloudapi.business.datasource.cache.task.toEntity
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.domain.util.ErrorHandling
import com.templateapp.cloudapi.business.domain.util.MessageType
import com.templateapp.cloudapi.business.domain.util.SuccessHandling
import com.templateapp.cloudapi.business.domain.util.UIComponentType
import com.templateapp.cloudapi.business.interactors.task.UpdateTask
import com.templateapp.cloudapi.datasource.cache.AppDatabaseFake
import com.templateapp.cloudapi.datasource.cache.BlogDaoFake
import com.templateapp.cloudapi.datasource.network.task.UpdateBlogResponses
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
 * 1. Success (updated the blog)
 * 2. Failure (AuthToken null)
 * 3. Failure (Don't have permission)
 * 4. Failure (Title must be 5+ chars)
 * 5. Failure (Body must be 50+ chars)
 * 6. Failure (Image too large - must be less than 2MB)
 * 7. Failure (Image aspect ratio not valid - width must be greater than height)
 */
class UpdateBlogTest {

    private val appDatabase = AppDatabaseFake()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var baseUrl: HttpUrl

    // system in test
    private lateinit var updateBlog: UpdateTask

    // dependencies
    private lateinit var service: OpenApiMainService
    private lateinit var cache: TaskDao

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
        updateBlog = UpdateTask(
            service = service,
            cache = cache,
        )
    }

    @Test
    fun updateSuccess() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(UpdateBlogResponses.updateSuccess)
        )

        // User Information
        val authToken = UpdateBlogResponses.authToken

        // Blog
        val blogPost = UpdateBlogResponses.blogPost

        // Insert into cache
        cache.insert(task = blogPost.toEntity())

        // Confirm the blog exists in the cache
        var cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog?.toTask() == blogPost)

        // Update the blog
        val title = UpdateBlogResponses.updatedTitle.toRequestBody("text/plain".toMediaTypeOrNull())
        val body = UpdateBlogResponses.updatedBody.toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartBody: MultipartBody.Part? = null // can be null since just a test
        val emissions = updateBlog.execute(
            authToken = authToken,
            slug = blogPost.slug,
            title = title,
            body = body,
            image = multipartBody
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm it was updated in the cache
        cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog?.toTask()?.title == UpdateBlogResponses.updatedTitle)
        assert(cachedBlog?.toTask()?.body == UpdateBlogResponses.updatedBody)

        // confirm second emission is a success message
        assert(emissions[1].data?.message == SuccessHandling.SUCCESS_TASK_UPDATED)
        assert(emissions[1].data?.uiComponentType is UIComponentType.None)
        assert(emissions[1].data?.messageType is MessageType.Success)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun updateFail_authTokenNull() = runBlocking {
        // User Information
        val authToken = null

        // Blog
        val blogPost = UpdateBlogResponses.blogPost

        // Insert into cache
        cache.insert(task = blogPost.toEntity())

        // Confirm the blog exists in the cache
        var cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog?.toTask() == blogPost)

        // Update the blog
        val title = UpdateBlogResponses.updatedTitle.toRequestBody("text/plain".toMediaTypeOrNull())
        val body = UpdateBlogResponses.updatedBody.toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartBody: MultipartBody.Part? = null // can be null since just a test
        val emissions = updateBlog.execute(
            authToken = authToken,
            slug = blogPost.slug,
            title = title,
            body = body,
            image = multipartBody
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm it was NOT updated in the cache
        cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog?.toTask()?.title == blogPost.title)
        assert(cachedBlog?.toTask()?.body == blogPost.body)

        // confirm second emission is a error dialog
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun updateFail_doNotHavePermission() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(UpdateBlogResponses.updateFail_dontHavePermission)
        )

        // User Information
        val authToken = UpdateBlogResponses.authToken

        // Blog
        val blogPost = UpdateBlogResponses.blogPost

        // Insert into cache
        cache.insert(task = blogPost.toEntity())

        // Confirm the blog exists in the cache
        var cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog?.toTask() == blogPost)

        // Update the blog
        val title = UpdateBlogResponses.updatedTitle.toRequestBody("text/plain".toMediaTypeOrNull())
        val body = UpdateBlogResponses.updatedBody.toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartBody: MultipartBody.Part? = null // can be null since just a test
        val emissions = updateBlog.execute(
            authToken = authToken,
            slug = blogPost.slug,
            title = title,
            body = body,
            image = multipartBody
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm it was NOT updated in the cache
        cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog?.toTask()?.title == blogPost.title)
        assert(cachedBlog?.toTask()?.body == blogPost.body)

        // confirm second emission is a error dialog
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_EDIT_TASK_NEED_PERMISSION)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun updateFail_titleLength() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(UpdateBlogResponses.updateFail_titleLength)
        )

        // User Information
        val authToken = UpdateBlogResponses.authToken

        // Blog
        val blogPost = UpdateBlogResponses.blogPost

        // Insert into cache
        cache.insert(task = blogPost.toEntity())

        // Confirm the blog exists in the cache
        var cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog?.toTask() == blogPost)

        // Update the blog
        val title = UpdateBlogResponses.updatedTitle.toRequestBody("text/plain".toMediaTypeOrNull())
        val body = UpdateBlogResponses.updatedBody.toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartBody: MultipartBody.Part? = null // can be null since just a test
        val emissions = updateBlog.execute(
            authToken = authToken,
            slug = blogPost.slug,
            title = title,
            body = body,
            image = multipartBody
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm it was NOT updated in the cache
        cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog?.toTask()?.title == blogPost.title)
        assert(cachedBlog?.toTask()?.body == blogPost.body)

        // confirm second emission is a error dialog
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_TASK_TITLE_LENGTH)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun updateFail_bodyLength() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(UpdateBlogResponses.updateFail_bodyLength)
        )

        // User Information
        val authToken = UpdateBlogResponses.authToken

        // Blog
        val blogPost = UpdateBlogResponses.blogPost

        // Insert into cache
        cache.insert(task = blogPost.toEntity())

        // Confirm the blog exists in the cache
        var cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog?.toTask() == blogPost)

        // Update the blog
        val title = UpdateBlogResponses.updatedTitle.toRequestBody("text/plain".toMediaTypeOrNull())
        val body = UpdateBlogResponses.updatedBody.toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartBody: MultipartBody.Part? = null // can be null since just a test
        val emissions = updateBlog.execute(
            authToken = authToken,
            slug = blogPost.slug,
            title = title,
            body = body,
            image = multipartBody
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm it was NOT updated in the cache
        cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog?.toTask()?.title == blogPost.title)
        assert(cachedBlog?.toTask()?.body == blogPost.body)

        // confirm second emission is a error dialog
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_TASK_BODY_LENGTH)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun updateFail_imageSize() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(UpdateBlogResponses.updateFail_imageSize)
        )

        // User Information
        val authToken = UpdateBlogResponses.authToken

        // Blog
        val blogPost = UpdateBlogResponses.blogPost

        // Insert into cache
        cache.insert(task = blogPost.toEntity())

        // Confirm the blog exists in the cache
        var cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog?.toTask() == blogPost)

        // Update the blog
        val title = UpdateBlogResponses.updatedTitle.toRequestBody("text/plain".toMediaTypeOrNull())
        val body = UpdateBlogResponses.updatedBody.toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartBody: MultipartBody.Part? = null // can be null since just a test
        val emissions = updateBlog.execute(
            authToken = authToken,
            slug = blogPost.slug,
            title = title,
            body = body,
            image = multipartBody
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm it was NOT updated in the cache
        cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog?.toTask()?.title == blogPost.title)
        assert(cachedBlog?.toTask()?.body == blogPost.body)

        // confirm second emission is a error dialog
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_TASK_IMAGE_SIZE)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun updateFail_imageAspectRatio() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(UpdateBlogResponses.updateFail_imageAspectRatio)
        )

        // User Information
        val authToken = UpdateBlogResponses.authToken

        // Blog
        val blogPost = UpdateBlogResponses.blogPost

        // Insert into cache
        cache.insert(task = blogPost.toEntity())

        // Confirm the blog exists in the cache
        var cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog?.toTask() == blogPost)

        // Update the blog
        val title = UpdateBlogResponses.updatedTitle.toRequestBody("text/plain".toMediaTypeOrNull())
        val body = UpdateBlogResponses.updatedBody.toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartBody: MultipartBody.Part? = null // can be null since just a test
        val emissions = updateBlog.execute(
            authToken = authToken,
            slug = blogPost.slug,
            title = title,
            body = body,
            image = multipartBody
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm it was NOT updated in the cache
        cachedBlog = cache.getTask(blogPost.id)
        assert(cachedBlog?.toTask()?.title == blogPost.title)
        assert(cachedBlog?.toTask()?.body == blogPost.body)

        // confirm second emission is a error dialog
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_TASK_IMAGE_ASPECT_RATIO)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }
}























