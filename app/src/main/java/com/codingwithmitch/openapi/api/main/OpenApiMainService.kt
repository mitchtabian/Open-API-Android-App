package com.codingwithmitch.openapi.api.main

import androidx.lifecycle.LiveData
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.util.GenericApiResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface OpenApiMainService {


    @GET("account/properties")
    fun getAccountProperties(
        @Header("Authorization") authorization: String
    ): LiveData<GenericApiResponse<AccountProperties>>

}









