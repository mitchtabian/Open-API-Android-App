package com.codingwithmitch.openapi.interactors.blog

import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistence.blog.BlogPostDao
import com.codingwithmitch.openapi.persistence.blog.toEntity
import com.codingwithmitch.openapi.util.*
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class DeleteBlogPost(
    private val service: OpenApiMainService,
    private val cache: BlogPostDao,
) {
    /**
     * If successful this will emit a string saying: 'deleted'
     */
    fun execute(
        authToken: AuthToken?,
        blogPost: BlogPost,
    ): Flow<DataState<Response>> = flow{
        if(authToken == null){
            throw Exception("Authentication token is invalid. Log out and log back in.")
        }
        // attempt delete from network
        val response = service.deleteBlogPost(
            "Token ${authToken.token!!}",
            blogPost.slug
        ).response
        if(response != SUCCESS_BLOG_DELETED){ // failure
            emit(DataState.error<Response>(
                response = Response(
                    message = response,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            ))
        }else{
            // delete from cache
            cache.deleteBlogPost(blogPost.toEntity())
            // Tell the UI it was successful
            emit(DataState.data<Response>(
                data = Response(
                    message = SUCCESS_BLOG_DELETED,
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ),
                response = null
            ))
        }
    }.catch { e ->
        e.printStackTrace()
        emit(DataState.error<Response>(
            response = Response(
                message = e.message,
                uiComponentType = UIComponentType.Dialog(),
                messageType = MessageType.Error()
            )
        ))
    }
}
















