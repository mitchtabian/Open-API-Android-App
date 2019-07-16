package com.codingwithmitch.openapi.api

import com.codingwithmitch.models.AuthToken
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OpenApiService {

    @POST("account/login")
    @FormUrlEncoded //  ----------------> Do I need this?
    fun login(
        @Field("username") email: String,
        @Field("password") password: String
    ): Call<AuthToken>


}
















