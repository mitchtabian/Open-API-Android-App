package com.codingwithmitch.openapi.api.main

import com.codingwithmitch.openapi.api.main.network_responses.AccountPropertiesResponse
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface OpenApiMainService {


    @GET("account/properties")
    suspend fun getAccountProperties(
        @Header("Authorization") authorization: String
    ): AccountPropertiesResponse

    @GET("account/properties")
    fun getAccountProperties2(
        @Header("Authorization") authorization: String
    ): Call<AccountPropertiesResponse>


}
















