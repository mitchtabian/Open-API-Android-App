package com.codingwithmitch.openapi.business.interactors.blog

import com.codingwithmitch.openapi.api.handleUseCaseException
import com.codingwithmitch.openapi.business.domain.models.BlogPost
import com.codingwithmitch.openapi.business.datasource.cache.blog.BlogPostDao
import com.codingwithmitch.openapi.business.datasource.cache.blog.toBlogPost
import com.codingwithmitch.openapi.business.domain.util.DataState
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling.Companion.ERROR_BLOG_UNABLE_TO_RETRIEVE
import com.codingwithmitch.openapi.business.domain.util.MessageType
import com.codingwithmitch.openapi.business.domain.util.Response
import com.codingwithmitch.openapi.business.domain.util.UIComponentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetBlogFromCache(
    private val cache: BlogPostDao,
) {

    fun execute(
        pk: Int,
    ): Flow<DataState<BlogPost>> = flow{
        emit(DataState.loading<BlogPost>())
        val blogPost = cache.getBlogPost(pk)?.toBlogPost()

        if(blogPost != null){
            emit(DataState.data(response = null, data = blogPost))
        }
        else{
            emit(DataState.error<BlogPost>(
                response = Response(
                    message = ERROR_BLOG_UNABLE_TO_RETRIEVE,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            ))
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}



















