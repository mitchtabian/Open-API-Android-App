package com.codingwithmitch.openapi.interactors.blog

import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.persistence.blog.BlogPostDao
import com.codingwithmitch.openapi.util.*
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.SUCCESS_BLOG_UPDATED
import kotlinx.coroutines.flow.Flow
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
        try {
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

            if(createUpdateResponse.response != SuccessHandling.SUCCESS_BLOG_DELETED){ // failure
                emit(DataState.error<Response>(
                    response = Response(
                        message = createUpdateResponse.response,
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    )
                ))
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
                        uiComponentType = UIComponentType.Toast(),
                        messageType = MessageType.Success()
                    ),
                    response = null,
                ))
            }

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









