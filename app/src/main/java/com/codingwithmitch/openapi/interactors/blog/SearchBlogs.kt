package com.codingwithmitch.openapi.interactors.blog

import com.codingwithmitch.openapi.api.handleUseCaseException
import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.api.main.toBlogPost
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistence.blog.*
import com.codingwithmitch.openapi.ui.main.blog.list.BlogFilterOptions
import com.codingwithmitch.openapi.ui.main.blog.list.BlogOrderOptions
import com.codingwithmitch.openapi.util.DataState
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
            throw Exception("Authentication token is invalid. Log out and log back in.")
        }
        // get Blogs from network
        val filterAndOrder = order.value + filter.value // Ex: -date_updated
        val blogs = service.searchListBlogPosts(
            "Token ${authToken.token}",
            query = query,
            ordering = filterAndOrder,
            page = page
        ).results.map { it.toBlogPost() }

        // Insert into cache
        for(blog in blogs){
            try{
                cache.insert(blog.toEntity())
            }catch (e: Exception){
                e.printStackTrace()
            }
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



















