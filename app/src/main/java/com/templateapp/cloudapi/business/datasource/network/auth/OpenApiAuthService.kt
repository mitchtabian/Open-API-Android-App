package com.templateapp.cloudapi.business.datasource.network.auth

import com.templateapp.cloudapi.business.datasource.network.auth.network_responses.LoginResponse
import com.templateapp.cloudapi.business.datasource.network.auth.network_responses.RegistrationResponse
import retrofit2.http.*

interface OpenApiAuthService {

    @POST("api/users/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @POST("api/users")
    @FormUrlEncoded
    suspend fun register(
        @Field("email") email: String,
        @Field("role") role: String,
    ): RegistrationResponse


}
