package com.codingwithmitch.openapi.business.interactors.blog

import com.codingwithmitch.openapi.api.handleUseCaseException
import com.codingwithmitch.openapi.business.datasource.network.main.OpenApiMainService
import com.codingwithmitch.openapi.business.domain.models.AuthToken
import com.codingwithmitch.openapi.business.datasource.cache.blog.BlogPostDao
import com.codingwithmitch.openapi.business.domain.util.*
import com.codingwithmitch.openapi.business.domain.util.SuccessHandling.Companion.SUCCESS_BLOG_UPDATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception

class UpdateBlogPost(
    private val service: OpenApiMainService,
    private val cache: BlogPostDao,
) {

    /**
     * If successful this will emit a string saying: 'updated'
     */
    fun execute(
        authToken: AuthToken?,
        slug: String,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?,
    ): Flow<DataState<Response>> = flow{
        emit(DataState.loading<Response>())
        if(authToken == null){
            throw Exception("Authentication token is invalid. Log out and log back in.")
        }
        // attempt update
        val createUpdateResponse = service.updateBlog(
            "Token ${authToken.token}",
            slug,
            title,
            body,
            image
        )

        if(createUpdateResponse.response != SuccessHandling.SUCCESS_BLOG_UPDATED){ // failure
            throw Exception(createUpdateResponse.response)
        }else{ // success
            cache.updateBlogPost(
                createUpdateResponse.pk,
                createUpdateResponse.title,
                createUpdateResponse.body,
                createUpdateResponse.image
            )
            // Tell the UI it was successful
            emit(DataState.data<Response>(
                data = Response(
                    message = SUCCESS_BLOG_UPDATED,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Success()
                ),
                response = null,
            ))
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}









