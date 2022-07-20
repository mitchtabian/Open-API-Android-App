package com.templateapp.cloudapi.business.datasource.network.auth

import android.content.res.Resources
import android.provider.Settings.Global.getString
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.datasource.network.auth.network_responses.LoginResponse
import com.templateapp.cloudapi.business.datasource.network.auth.network_responses.RegistrationResponse
import com.templateapp.cloudapi.business.domain.util.Constants.Companion.API
import retrofit2.http.*

interface OpenApiAuthService {


    @POST(API + "/users/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @POST(API+"/users")
    @FormUrlEncoded
    suspend fun register(
        @Field("email") email: String,
        @Field("role") role: String,
    ): RegistrationResponse


}
