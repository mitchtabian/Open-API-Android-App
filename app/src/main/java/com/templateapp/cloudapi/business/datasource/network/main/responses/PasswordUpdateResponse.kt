package com.templateapp.cloudapi.business.datasource.network.main.responses

import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.network.responseObjects.User

class PasswordUpdateResponse(

    @SerializedName("response")
    val response: String?,

    @SerializedName("error")
    val error: String?,

    @SerializedName("user")
    val user: User?
)













