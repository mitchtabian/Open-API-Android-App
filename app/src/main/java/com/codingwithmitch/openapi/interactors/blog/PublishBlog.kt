package com.codingwithmitch.openapi.interactors.blog

import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.api.main.responses.toBlogPost
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.persistence.blog.BlogPostDao
import com.codingwithmitch.openapi.persistence.blog.toEntity
import com.codingwithmitch.openapi.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception

class PublishBlog(
    private val service: OpenApiMainService,
    private val cache: BlogPostDao,
){
    fun execute(
        authToken: AuthToken?,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?,
    ): Flow<DataState<Response>> = flow {
        try {
            if(authToken == null){
                throw Exception("Authentication token is invalid. Log out and log back in.")
            }
            // attempt update
            val createResponse = service.createBlog(
                "Token ${authToken.token}",
                title,
                body,
                image
            )

            // If they don't have a paid membership account it will still return a 200 with failure message
            // Need to account for that
            if (createResponse.response.equals(SuccessHandling.RESPONSE_MUST_BECOME_CODINGWITHMITCH_MEMBER)) { // failure
                throw Exception(SuccessHandling.RESPONSE_MUST_BECOME_CODINGWITHMITCH_MEMBER)
            }

            // insert the new blog into the cache
            cache.insert(createResponse.toBlogPost().toEntity())

            // Tell the UI it was successful
            emit(DataState.data<Response>(
                data = Response(
                    message = SuccessHandling.SUCCESS_BLOG_CREATED,
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ),
                response = null,
            ))

        }catch (e: Exception){
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
}






















