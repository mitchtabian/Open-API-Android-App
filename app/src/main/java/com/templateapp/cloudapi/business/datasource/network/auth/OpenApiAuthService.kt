package com.templateapp.cloudapi.business.datasource.network.auth

import com.templateapp.cloudapi.business.datasource.network.auth.network_responses.LoginResponse
import com.templateapp.cloudapi.business.datasource.network.auth.network_responses.RegistrationResponse
import retrofit2.http.*

interface OpenApiAuthService {

    @POST("users/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @POST("users")
    @FormUrlEncoded
    suspend fun register(
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("password") password: String,
        @Field("confirm_password") confirm_password: String
    ): RegistrationResponse

}
