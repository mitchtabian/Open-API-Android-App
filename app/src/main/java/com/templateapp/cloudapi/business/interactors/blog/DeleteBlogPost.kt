package com.templateapp.cloudapi.business.interactors.blog

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.domain.models.BlogPost
import com.templateapp.cloudapi.business.datasource.cache.blog.BlogPostDao
import com.templateapp.cloudapi.business.datasource.cache.blog.toEntity
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_DELETE_BLOG_DOES_NOT_EXIST
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.GENERIC_ERROR
import com.templateapp.cloudapi.business.domain.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
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
        emit(DataState.loading<Response>())
        if(authToken == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        // attempt delete from network
        val response = service.deleteBlogPost(
            "${authToken.token}",
            blogPost.id
        )
        if(response.response == GENERIC_ERROR
            && response.errorMessage != ERROR_DELETE_BLOG_DOES_NOT_EXIST){ // failure
            throw Exception(response.errorMessage)
        }else{
            // delete from cache
            cache.deleteBlogPost(blogPost.toEntity())
            // Tell the UI it was successful
            emit(DataState.data<Response>(
                data = Response(
                    message = SUCCESS_BLOG_DELETED,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Success()
                ),
                response = null
            ))
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}
















