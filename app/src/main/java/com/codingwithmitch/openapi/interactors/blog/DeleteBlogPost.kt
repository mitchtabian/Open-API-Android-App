package com.codingwithmitch.openapi.interactors.blog

import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistence.blog.BlogPostDao
import com.codingwithmitch.openapi.persistence.blog.toEntity
import com.codingwithmitch.openapi.util.*
import kotlinx.coroutines.flow.Flow
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
    ): Flow<DataState<String>> = flow{
        if(authToken == null){
            emit(DataState.error<String>(
                response = Response(
                    message = "Authentication token is invalid. Log out and log back in.",
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            ))
        }
        try {
            // attempt delete from network
            val response = service.deleteBlogPost(
                "Token ${authToken!!.token!!}",
                blogPost.slug
            ).response
            if(response != SuccessHandling.SUCCESS_BLOG_DELETED){ // failure
                emit(DataState.error<String>(
                    response = Response(
                        message = response,
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    )
                ))
            }else{ // success
                cache.deleteBlogPost(blogPost.toEntity()) // delete from cache
                emit(DataState.data<String>(
                    response = Response(
                        message = SuccessHandling.SUCCESS_BLOG_DELETED,
                        uiComponentType = UIComponentType.Toast(),
                        messageType = MessageType.Success()
                    ),
                ))
            }

        }catch (e: Exception){
            e.printStackTrace()
            emit(DataState.error<String>(
                response = Response(
                    message = e.message,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            ))
        }
    }
}
















