package com.templateapp.cloudapi.business.interactors.blog

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.datasource.network.main.toBlogPost
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.domain.models.BlogPost
import com.templateapp.cloudapi.business.datasource.cache.blog.*
import com.templateapp.cloudapi.business.domain.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.templateapp.cloudapi.presentation.main.blog.list.BlogFilterOptions
import com.templateapp.cloudapi.presentation.main.blog.list.BlogOrderOptions
import com.templateapp.cloudapi.business.domain.util.DataState
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import com.templateapp.cloudapi.business.domain.util.MessageType
import com.templateapp.cloudapi.business.domain.util.Response
import com.templateapp.cloudapi.business.domain.util.UIComponentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SearchBlogs(
    private val service: OpenApiMainService,
    private val cache: BlogPostDao,
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        query: String,
        page: Int,
        filter: BlogFilterOptions,
        order: BlogOrderOptions,
    ): Flow<DataState<List<BlogPost>>> = flow {
        emit(DataState.loading<List<BlogPost>>())
        if(authToken == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        // get Blogs from network
        val filterAndOrder = filter.value + order.value // Ex: modifiedAt:desc

        try{ // catch network exception
            val blogs = service.searchListBlogPosts(
                "${authToken.token}",
                query = query,
                sortBy = filterAndOrder,
                skip = (page - 1) * PAGINATION_PAGE_SIZE,
                limit = PAGINATION_PAGE_SIZE
            ).results.map { it.toBlogPost() }

            // Insert into cache
            for(blog in blogs){
                try{
                    cache.insert(blog.toEntity())
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }catch (e: Exception){
            emit(
                DataState.error<List<BlogPost>>(
                    response = Response(
                        message = "Unable to update the cache.",
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }

        // emit from cache
        val cachedBlogs = cache.returnOrderedBlogQuery(
            query = query,
            filterAndOrder = filterAndOrder,
            page = page
        ).map { it.toBlogPost() }

        emit(DataState.data(response = null, data = cachedBlogs))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}



















