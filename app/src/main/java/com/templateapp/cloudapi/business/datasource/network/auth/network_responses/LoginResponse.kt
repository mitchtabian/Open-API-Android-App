package com.templateapp.cloudapi.business.datasource.network.auth.network_responses

import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.network.responseObjects.User

class LoginResponse(

    @SerializedName("user")
    var user: User,

    @SerializedName("error")
    var errorMessage: String?,

    @SerializedName("token")
    var token: String
)
