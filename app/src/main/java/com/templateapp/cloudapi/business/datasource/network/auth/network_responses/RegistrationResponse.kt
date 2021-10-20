package com.templateapp.cloudapi.business.datasource.network.auth.network_responses

import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.network.responseObjects.User

class RegistrationResponse(

    @SerializedName("user")
    var user: User,

    @SerializedName("token")
    var token: String,

    @SerializedName("error")
    var error: String

)