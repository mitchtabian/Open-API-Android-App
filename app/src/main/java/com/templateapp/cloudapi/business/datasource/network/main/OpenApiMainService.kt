package com.templateapp.cloudapi.business.datasource.network.main

import androidx.lifecycle.LiveData
import com.templateapp.cloudapi.business.datasource.network.GenericResponse
import com.templateapp.cloudapi.business.datasource.network.main.responses.BlogCreateUpdateResponse
import com.templateapp.cloudapi.business.datasource.network.main.responses.BlogListSearchResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface OpenApiMainService {


    @GET("account/properties")
    suspend fun getAccount(
        @Header("Authorization") authorization: String
    ): AccountDto

    @PUT("account/properties/update")
    @FormUrlEncoded
    suspend fun updateAccount(
        @Header("Authorization") authorization: String,
        @Field("email") email: String,
        @Field("username") username: String
    ): GenericResponse

    @PUT("account/change_password/")
    @FormUrlEncoded
    suspend fun updatePassword(
        @Header("Authorization") authorization: String,
        @Field("old_password") currentPassword: String,
        @Field("new_password") newPassword: String,
        @Field("confirm_new_password") confirmNewPassword: String
    ): GenericResponse

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

    @GET("blog/{slug}/is_author")
    suspend fun isAuthorOfBlogPost(
        @Header("Authorization") authorization: String,
        @Path("slug") slug: String
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

    @Multipart
    @POST("blog/create")
    suspend fun createBlog(
        @Header("Authorization") authorization: String,
        @Part("title") title: RequestBody,
        @Part("body") body: RequestBody,
        @Part image: MultipartBody.Part?
    ): BlogCreateUpdateResponse

    @GET("blog/{slug}")
    suspend fun getBlog(
        @Header("Authorization") authorization: String,
        @Path("slug") slug: String,
    ): BlogPostDto?
}

