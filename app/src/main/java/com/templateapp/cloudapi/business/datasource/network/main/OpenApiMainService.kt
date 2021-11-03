package com.templateapp.cloudapi.business.datasource.network.main

import androidx.lifecycle.LiveData
import com.templateapp.cloudapi.business.datasource.network.GenericResponse
import com.templateapp.cloudapi.business.datasource.network.main.responses.AccountUpdateResponse
import com.templateapp.cloudapi.business.datasource.network.main.responses.BlogCreateUpdateResponse
import com.templateapp.cloudapi.business.datasource.network.main.responses.BlogListSearchResponse
import com.templateapp.cloudapi.business.datasource.network.main.responses.PasswordUpdateResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface OpenApiMainService {


    @GET("users/me")
    suspend fun getAccount(
        @Header("Authorization") authorization: String
    ): AccountDto

    @PATCH("users/me")
    @FormUrlEncoded
    suspend fun updateAccount(
        @Header("Authorization") authorization: String,
        @Field("email") email: String,
        @Field("name") name: String
    ): AccountUpdateResponse

    @PATCH("users/me/change_password")
    @FormUrlEncoded
    suspend fun updatePassword(
        @Header("Authorization") authorization: String,
        @Field("current_password") currentPassword: String,
        @Field("new_password") newPassword: String,
        @Field("confirm_new_password") confirmNewPassword: String
    ): PasswordUpdateResponse

    /* Get a list of all the tasks */
    @GET("all_tasks")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun searchListBlogPosts(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int,
        @Query("search") query: String,
        @Query("sortBy") sortBy: String
    ): BlogListSearchResponse

    /* Get the owner of the task - is it me? */
    @GET("tasks/{id}/is_owner")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun isAuthorOfBlogPost(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): GenericResponse


    /* Delete the task */
    @DELETE("tasks/{id}")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun deleteBlogPost(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): GenericResponse

    // Update the task
    @Multipart
    @PATCH("tasks/{id}")
    suspend fun updateBlog(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("completed") completed: Boolean,
        @Part image: MultipartBody.Part?
    ): BlogCreateUpdateResponse

    /* Create the task */
    @Multipart
    @POST("tasks")
    suspend fun createBlog(
        @Header("Authorization") authorization: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("completed") completed: Boolean,
        @Part image: MultipartBody.Part?
    ): BlogCreateUpdateResponse

    @GET("tasks/{id}")
    suspend fun getBlog(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
    ): BlogPostDto?
}

