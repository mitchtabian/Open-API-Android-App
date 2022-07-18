package com.templateapp.cloudapi.business.datasource.network.main

import com.templateapp.cloudapi.business.datasource.network.GenericResponse
import com.templateapp.cloudapi.business.datasource.network.main.responses.*
import com.templateapp.cloudapi.business.domain.models.Role
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface OpenApiMainService {


    @GET("api/users/me")
    suspend fun getAccount(
        @Header("Authorization") authorization: String
    ): AccountDto

    @GET("api/users/{id}")
    suspend fun getAccountById(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): AccountDto

    @PATCH("api/users/me")
    @FormUrlEncoded
    suspend fun updateAccount(
        @Header("Authorization") authorization: String,
        @Field("email") email: String,
        @Field("name") name: String
    ): AccountUpdateResponse

    @PATCH("api/users/update")
    @FormUrlEncoded
    suspend fun changeAccount(
        @Header("Authorization") authorization: String,
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("age") age: Int,
        @Field("enabled") enabled: Boolean,
        @Field("role") role: String,
        @Field("initEmail") initEmail: String,
        @Field("initName") initName: String
    ): AccountUpdateResponse

    @PATCH("api/users/me/change_password")
    @FormUrlEncoded
    suspend fun updatePassword(
        @Header("Authorization") authorization: String,
        @Field("current_password") currentPassword: String,
        @Field("new_password") newPassword: String,
        @Field("confirm_new_password") confirmNewPassword: String
    ): PasswordUpdateResponse

    /* Get a list of all the tasks */
    @GET("api/all_tasks")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun searchListTasks(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int,
        @Query("search") query: String,
        @Query("sortBy") sortBy: String
    ): TaskListSearchResponse

    /* Get the owner of the task - is it me? */
    @GET("api/tasks/{id}/is_owner")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun isOwnerOfTask(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): GenericResponse


    /* Delete the task */
    @DELETE("api/tasks/{id}")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun deleteTask(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): GenericResponse

    // Update the task
    @Multipart
    @PATCH("api/tasks/{id}")
    suspend fun updateTask(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("completed") completed: Boolean,
        @Part image: MultipartBody.Part?
    ): TaskCreateUpdateResponse

    /* Create the task */
    @Multipart
    @POST("api/tasks")
    suspend fun createTask(
        @Header("Authorization") authorization: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("completed") completed: Boolean,
        @Part image: MultipartBody.Part?
    ): TaskCreateUpdateResponse

    @GET("api/tasks/{id}")
    suspend fun getTask(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
    ): TaskDto?


    @GET("api/userNumber")
    fun getAllUsers(
    ): AllUsersResponse

    @GET("api/all_users")
    suspend fun getAllUsers(
        @Header("Authorization") authorization: String,
        @Query("skip") skip: Int,
        @Query("limit") limit: Int,
    ): UserListResponse

    @GET("api/roles")
    suspend fun getAllRoles(
        @Header("Authorization") authorization: String,
    ): RolesResponse

}

