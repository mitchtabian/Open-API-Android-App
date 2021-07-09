package com.codingwithmitch.openapi.interactors.blog

import com.codingwithmitch.openapi.business.datasource.cache.blog.BlogPostDao
import com.codingwithmitch.openapi.business.datasource.cache.blog.toBlogPost
import com.codingwithmitch.openapi.business.datasource.cache.blog.toEntity
import com.codingwithmitch.openapi.business.datasource.network.main.OpenApiMainService
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling
import com.codingwithmitch.openapi.business.domain.util.MessageType
import com.codingwithmitch.openapi.business.domain.util.SuccessHandling
import com.codingwithmitch.openapi.business.domain.util.UIComponentType
import com.codingwithmitch.openapi.business.interactors.blog.DeleteBlogPost
import com.codingwithmitch.openapi.datasource.cache.AppDatabaseFake
import com.codingwithmitch.openapi.datasource.cache.BlogDaoFake
import com.codingwithmitch.openapi.datasource.network.blog.DeleteResponses
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
 * 1. Delete success
 * 2. Delete failure (do not have permission to delete someone else's blog)
 * 3. Delete failure (blog does not exist on server but does exist in cache)
 * 4. Delete failure (Not a codingwithmitch.com member)
 */
class DeleteTest {

    private val appDatabase = AppDatabaseFake()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var baseUrl: HttpUrl

    // system in test
    private lateinit var deleteBlogPost: DeleteBlogPost

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
        deleteBlogPost = DeleteBlogPost(
            service = service,
            cache = cache,
        )
    }

    @Test
    fun deleteSuccess() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(DeleteResponses.deleteSuccess)
        )

        // User Information
        val authToken = DeleteResponses.authToken

        // Blog
        val blogPost = DeleteResponses.blogPost

        // Ensure the blog exists in the cache before deleting
        cache.insert(blogPost = blogPost.toEntity())

        // confirm it exists in the cache
        var cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog?.toBlogPost() == blogPost)

        // delete the blog
        val emissions = deleteBlogPost.execute(
            authToken = authToken,
            blogPost = blogPost
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm it was deleted from the cache
        cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog == null)

        // confirm second emission is a success message
        assert(emissions[1].data?.message == SuccessHandling.SUCCESS_BLOG_DELETED)
        assert(emissions[1].data?.uiComponentType is UIComponentType.None)
        assert(emissions[1].data?.messageType is MessageType.Success)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun deleteFail_needPermission() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(DeleteResponses.deleteFail_needPermission)
        )

        // User Information
        val authToken = DeleteResponses.authToken

        // Blog
        val blogPost = DeleteResponses.blogPost

        // Ensure the blog exists in the cache before deleting
        cache.insert(blogPost = blogPost.toEntity())

        // confirm it exists in the cache
        var cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog?.toBlogPost() == blogPost)

        // attempt to delete the blog
        val emissions = deleteBlogPost.execute(
            authToken = authToken,
            blogPost = blogPost
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm it was NOT deleted from the cache
        cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog?.toBlogPost() == blogPost)

        // confirm second emission is an error dialog
        assert(emissions[1].data == null)
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_DELETE_BLOG_NEED_PERMISSION)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    /**
     * Blog exists in cache but does not exist on server.
     * We need to delete from cache.
     */
    @Test
    fun deleteFail_blogDoesNotExist() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(DeleteResponses.deleteFail_blogDoesNotExist)
        )

        // User Information
        val authToken = DeleteResponses.authToken

        // Blog
        val blogPost = DeleteResponses.blogPost

        // Ensure the blog exists in the cache before deleting
        cache.insert(blogPost = blogPost.toEntity())

        // confirm it exists in the cache
        var cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog?.toBlogPost() == blogPost)

        // attempt to delete the blog
        val emissions = deleteBlogPost.execute(
            authToken = authToken,
            blogPost = blogPost
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm it was deleted from the cache
        cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog == null)

        // confirm second emission is a success message
        assert(emissions[1].data?.message == SuccessHandling.SUCCESS_BLOG_DELETED)
        assert(emissions[1].data?.uiComponentType is UIComponentType.None)
        assert(emissions[1].data?.messageType is MessageType.Success)

        // loading done
        assert(!emissions[1].isLoading)
    }

    /**
     * Blog exists in cache but does not exist on server.
     * We need to delete from cache.
     */
    @Test
    fun deleteFail_notCwmMember() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(DeleteResponses.deleteFail_notCwmMember)
        )

        // User Information
        val authToken = DeleteResponses.authToken

        // Blog
        val blogPost = DeleteResponses.blogPost

        // Ensure the blog exists in the cache before deleting
        cache.insert(blogPost = blogPost.toEntity())

        // confirm it exists in the cache
        var cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog?.toBlogPost() == blogPost)

        // attempt to delete the blog
        val emissions = deleteBlogPost.execute(
            authToken = authToken,
            blogPost = blogPost
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm it was NOT deleted from the cache
        cachedBlog = cache.getBlogPost(blogPost.pk)
        assert(cachedBlog?.toBlogPost() == blogPost)

        // confirm second emission is an error dialog
        assert(emissions[1].data == null)
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_NOT_CWM_MEMBER)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }
}






















