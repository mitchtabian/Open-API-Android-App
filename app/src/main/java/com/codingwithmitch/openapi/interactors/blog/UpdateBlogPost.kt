package com.codingwithmitch.openapi.interactors.blog

import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.persistence.blog.BlogPostDao
import com.codingwithmitch.openapi.util.*
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
        else{
            try {
                // attempt update
                val createUpdateResponse = service.updateBlog(
                    "Token ${authToken.token!!}",
                    slug,
                    title,
                    body,
                    image
                )

                if(createUpdateResponse.response != SuccessHandling.SUCCESS_BLOG_DELETED){ // failure
                    emit(DataState.error<String>(
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
                    emit(DataState.data<String>(
                        response = Response(
                            message = SuccessHandling.SUCCESS_BLOG_UPDATED,
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
}









