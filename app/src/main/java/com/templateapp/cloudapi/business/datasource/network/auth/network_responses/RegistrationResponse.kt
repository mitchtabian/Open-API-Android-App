package com.templateapp.cloudapi.business.datasource.network.auth.network_responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.network.responseObjects.User

class RegistrationResponse(

    @SerializedName("error")
    var error: String,

    @SerializedName("success")
    var success: String,

    @SerializedName("response")
    @Expose
    val response: String?,


)