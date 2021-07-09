package com.codingwithmitch.openapi.interactors.blog

import com.codingwithmitch.openapi.business.datasource.cache.blog.BlogPostDao
import com.codingwithmitch.openapi.business.datasource.network.main.OpenApiMainService
import com.codingwithmitch.openapi.business.domain.models.BlogPost
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling
import com.codingwithmitch.openapi.business.domain.util.MessageType
import com.codingwithmitch.openapi.business.domain.util.UIComponentType
import com.codingwithmitch.openapi.business.interactors.blog.SearchBlogs
import com.codingwithmitch.openapi.datasource.cache.AppDatabaseFake
import com.codingwithmitch.openapi.datasource.cache.BlogDaoFake
import com.codingwithmitch.openapi.datasource.network.blog.SearchBlogsResponses
import com.codingwithmitch.openapi.presentation.main.blog.list.BlogFilterOptions
import com.codingwithmitch.openapi.presentation.main.blog.list.BlogOrderOptions
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
 * 1. Success (Retrieve 10 blogs)
 * 2. Success (Retrieve 3 blogs)
 * 3. Success (Retrieve empty list)
 * 4. Failure (AuthToken null)
 * 5. Failure (Random error)
 * 6. Failure (Malformed data)
 */
class SearchBlogsTest {

    private val appDatabase = AppDatabaseFake()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var baseUrl: HttpUrl

    // system in test
    private lateinit var searchBlogs: SearchBlogs

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
        searchBlogs = SearchBlogs(
            service = service,
            cache = cache,
        )
    }

    @Test
    fun success_10Blogs() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(SearchBlogsResponses.success_10Blogs)
        )

        // User Information
        val authToken = SearchBlogsResponses.authToken

        // Confirm there are no blogs in the cache
        var cachedBlogs = cache.getAllBlogPosts("", 1, 10)
        assert(cachedBlogs.isEmpty())

        // Execute the use case
        val emissions = searchBlogs.execute(
            authToken = authToken,
            query = "",
            page = 1,
            filter = BlogFilterOptions.DATE_UPDATED,
            order = BlogOrderOptions.DESC,
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm the blogs were inserted into the cache
        cachedBlogs = cache.getAllBlogPosts("", 1, 10)
        assert(cachedBlogs.size == 10)

        // confirm second emission is a list of data and they are blog posts
        assert(emissions[1].data?.size == 10)
        assert(emissions[1].data?.get(0) != null)
        assert(emissions[1].data?.get(0) is BlogPost)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun success_3Blogs() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(SearchBlogsResponses.success3Blogs)
        )

        // User Information
        val authToken = SearchBlogsResponses.authToken

        // Confirm there are no blogs in the cache
        var cachedBlogs = cache.getAllBlogPosts("", 1, 10)
        assert(cachedBlogs.isEmpty())

        // Execute the use case
        val emissions = searchBlogs.execute(
            authToken = authToken,
            query = "",
            page = 1,
            filter = BlogFilterOptions.DATE_UPDATED,
            order = BlogOrderOptions.DESC,
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm the blogs were inserted into the cache
        cachedBlogs = cache.getAllBlogPosts("", 1, 10)
        assert(cachedBlogs.size == 3)

        // confirm second emission is a list of data and they are blog posts
        assert(emissions[1].data?.size == 3 )
        assert(emissions[1].data?.get(0) != null)
        assert(emissions[1].data?.get(0) is BlogPost)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun success_emptyList() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(SearchBlogsResponses.successEmptyList)
        )

        // User Information
        val authToken = SearchBlogsResponses.authToken

        // Confirm there are no blogs in the cache
        var cachedBlogs = cache.getAllBlogPosts("", 1, 10)
        assert(cachedBlogs.isEmpty())

        // Execute the use case
        val emissions = searchBlogs.execute(
            authToken = authToken,
            query = "",
            page = 1,
            filter = BlogFilterOptions.DATE_UPDATED,
            order = BlogOrderOptions.DESC,
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm cache is still empty
        cachedBlogs = cache.getAllBlogPosts("", 1, 10)
        assert(cachedBlogs.size == 0)

        // confirm second emission is empty list
        assert(emissions[1].data?.size == 0 )

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun failure_authTokenNull() = runBlocking {
        // User Information
        val authToken = null

        // Confirm there are no blogs in the cache
        var cachedBlogs = cache.getAllBlogPosts("", 1, 10)
        assert(cachedBlogs.isEmpty())

        // Execute the use case
        val emissions = searchBlogs.execute(
            authToken = authToken,
            query = "",
            page = 1,
            filter = BlogFilterOptions.DATE_UPDATED,
            order = BlogOrderOptions.DESC,
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm cache is still empty
        cachedBlogs = cache.getAllBlogPosts("", 1, 10)
        assert(cachedBlogs.size == 0)

        // confirm second emission is a error dialog
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun failure_randomError() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(SearchBlogsResponses.failure_randomError)
        )

        // User Information
        val authToken = SearchBlogsResponses.authToken

        // Confirm there are no blogs in the cache
        var cachedBlogs = cache.getAllBlogPosts("", 1, 10)
        assert(cachedBlogs.isEmpty())

        // Execute the use case
        val emissions = searchBlogs.execute(
            authToken = authToken,
            query = "",
            page = 1,
            filter = BlogFilterOptions.DATE_UPDATED,
            order = BlogOrderOptions.DESC,
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm cache is still empty
        cachedBlogs = cache.getAllBlogPosts("", 1, 10)
        assert(cachedBlogs.size == 0)

        // confirm second emission is a error dialog
        // don't care what the error is, just show it
        assert(emissions[1].stateMessage?.response?.message != null)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun failure_malformedData() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(SearchBlogsResponses.failure_malformedData)
        )

        // User Information
        val authToken = SearchBlogsResponses.authToken

        // Confirm there are no blogs in the cache
        var cachedBlogs = cache.getAllBlogPosts("", 1, 10)
        assert(cachedBlogs.isEmpty())

        // Execute the use case
        val emissions = searchBlogs.execute(
            authToken = authToken,
            query = "",
            page = 1,
            filter = BlogFilterOptions.DATE_UPDATED,
            order = BlogOrderOptions.DESC,
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm cache is still empty
        cachedBlogs = cache.getAllBlogPosts("", 1, 10)
        assert(cachedBlogs.size == 0)

        // confirm second emission is a error dialog
        // don't care what the error is, just show it
        assert(emissions[1].stateMessage?.response?.message != null)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }
}























