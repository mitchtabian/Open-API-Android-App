package com.codingwithmitch.openapi.interactors.blog

import com.codingwithmitch.openapi.api.handleUseCaseException
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistence.blog.BlogPostDao
import com.codingwithmitch.openapi.persistence.blog.toBlogPost
import com.codingwithmitch.openapi.util.DataState
import com.codingwithmitch.openapi.util.MessageType
import com.codingwithmitch.openapi.util.Response
import com.codingwithmitch.openapi.util.UIComponentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

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
                    message = "Unable to retrieve the blog post. Try reselecting it from the list.",
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            ))
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}



















