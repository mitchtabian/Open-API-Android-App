package com.codingwithmitch.openapi.api.auth

import com.codingwithmitch.openapi.api.auth.network_responses.RegistrationResponse
import com.codingwithmitch.openapi.models.AuthToken
import retrofit2.Call
import retrofit2.http.*

interface OpenApiAuthService {

    @POST("account/login")
    @FormUrlEncoded //  ----------------> Do I need this?
    fun login(
        @Field("username") email: String,
        @Field("password") password: String
    ): Call<AuthToken>


    @POST("account/register")
    @FormUrlEncoded
    fun register(
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("password2") password2: String
    ): Call<RegistrationResponse>


    @GET("account/check_if_account_exists/{email}")
    fun confirmAccountExists(
        @Path("email") email: String
    )
}
















