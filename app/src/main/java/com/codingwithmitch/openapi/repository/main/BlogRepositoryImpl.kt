package com.codingwithmitch.openapi.repository.main


import android.util.Log
import com.codingwithmitch.openapi.api.GenericResponse
import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.api.main.responses.BlogCreateUpdateResponse
import com.codingwithmitch.openapi.api.main.responses.BlogListSearchResponse
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistence.BlogPostDao
import com.codingwithmitch.openapi.persistence.returnOrderedBlogQuery
import com.codingwithmitch.openapi.repository.buildError
import com.codingwithmitch.openapi.repository.safeApiCall
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState.*
import com.codingwithmitch.openapi.util.*
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.RESPONSE_HAS_PERMISSION_TO_EDIT
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.RESPONSE_NO_PERMISSION_TO_EDIT
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class BlogRepositoryImpl
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
): BlogRepository
{

    override fun updateBlogPost(
        authToken: AuthToken,
        slug: String,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?,
        stateEvent: StateEvent
    ) = flow{

        val apiResult = safeApiCall(IO){
            openApiMainService.updateBlog(
                "Token ${authToken.token!!}",
                slug,
                title,
                body,
                image
            )
        }
        emit(
            object: ApiResponseHandler<BlogViewState, BlogCreateUpdateResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ){
                override suspend fun handleSuccess(resultObj: BlogCreateUpdateResponse): DataState<BlogViewState> {

                    val updatedBlogPost = resultObj.toBlogPost()

                    blogPostDao.updateBlogPost(
                        updatedBlogPost.pk,
                        updatedBlogPost.title,
                        updatedBlogPost.body,
                        updatedBlogPost.image
                    )

                    return DataState.data(
                        response = Response(
                            message = resultObj.response,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Success()
                        ),
                        data =  BlogViewState(
                            viewBlogFields = ViewBlogFields(
                                blogPost = updatedBlogPost
                            ),
                            updatedBlogFields = UpdatedBlogFields(
                                updatedBlogTitle = updatedBlogPost.title,
                                updatedBlogBody = updatedBlogPost.body,
                                updatedImageUri = null
                            )
                        ),
                        stateEvent = stateEvent
                    )

                }

            }.getResult()
        )
    }



}

















