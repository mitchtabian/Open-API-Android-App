package com.codingwithmitch.openapi.business.interactors.blog

import com.codingwithmitch.openapi.api.handleUseCaseException
import com.codingwithmitch.openapi.business.datasource.network.main.OpenApiMainService
import com.codingwithmitch.openapi.business.domain.models.AuthToken
import com.codingwithmitch.openapi.business.domain.util.DataState
import com.codingwithmitch.openapi.business.domain.util.SuccessHandling.Companion.RESPONSE_HAS_PERMISSION_TO_EDIT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class IsAuthorOfBlogPost(
    private val service: OpenApiMainService,
) {

    fun execute(
        authToken: AuthToken?,
        slug: String,
    ): Flow<DataState<Boolean>> = flow {
        emit(DataState.loading<Boolean>())
        if(authToken == null){
            throw Exception("Authentication token is invalid. Log out and log back in.")
        }
        val response = service.isAuthorOfBlogPost(
            "Token ${authToken.token}",
            slug
        )
        if(response.response == RESPONSE_HAS_PERMISSION_TO_EDIT){
            emit(DataState.data(response = null, true))
        }else{
            emit(DataState.data(response = null, false))
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}



























