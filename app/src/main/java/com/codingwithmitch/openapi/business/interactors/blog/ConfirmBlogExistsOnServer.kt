package com.codingwithmitch.openapi.business.interactors.blog

import com.codingwithmitch.openapi.api.handleUseCaseException
import com.codingwithmitch.openapi.business.datasource.cache.blog.BlogPostDao
import com.codingwithmitch.openapi.business.datasource.cache.blog.toBlogPost
import com.codingwithmitch.openapi.business.datasource.network.main.OpenApiMainService
import com.codingwithmitch.openapi.business.domain.models.AuthToken
import com.codingwithmitch.openapi.business.domain.models.BlogPost
import com.codingwithmitch.openapi.business.domain.util.*
import com.codingwithmitch.openapi.business.domain.util.SuccessHandling.Companion.SUCCESS_BLOG_DOES_NOT_EXIST_IN_CACHE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

/**
 * If a blog exist in the cache but does not exist on server, we need to delete from cache.
 */
class ConfirmBlogExistsOnServer(
    private val service: OpenApiMainService,
    private val cache: BlogPostDao
) {

    fun execute(
        authToken: AuthToken?,
        pk: Int,
        slug: String,
    ): Flow<DataState<Response>> =  flow {
        emit(DataState.loading<Response>())
        val cachedBlog = cache.getBlogPost(pk)
        if(cachedBlog == null){
            // It doesn't exist in cache. Finish.
            emit(DataState.data<Response>(
                response = Response(
                    message = SUCCESS_BLOG_DOES_NOT_EXIST_IN_CACHE,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Success()
                )
            ))
        }else{
            if(authToken == null){
                throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
            }
            // confirm it exists on server (throws 404 if does not exist)
            val blogPost = try {
                service.getBlog(
                    authorization = "Token ${authToken.token}",
                    slug = slug,
                )
            }catch (e1: Exception){
                e1.printStackTrace()
                null
            }
            // if it exists on server but not in cache. Delete from cache and emit error.
            if(blogPost == null){
                cache.deleteBlogPost(pk)
                emit(DataState.error<Response>(
                    response = Response(
                        message = ErrorHandling.ERROR_BLOG_DOES_NOT_EXIST,
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    )
                ))
            }else{ // if it exists in the cache and on the server. Everything is fine.
                emit(DataState.data<Response>(
                    data = Response(
                        message = SuccessHandling.SUCCESS_BLOG_EXISTS_ON_SERVER,
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Success()
                    ),
                    response = null,
                ))
            }
        }

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}
















