package com.codingwithmitch.openapi.api.main.network_responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AccountPropertiesResponse(

    @SerializedName("pk")
    @Expose
    var pk: Int,

    @SerializedName("email")
    @Expose
    var email: String,

    @SerializedName("username")
    @Expose
    var username: String,

    @SerializedName("detail")
    @Expose
    var detail: String

)

{
    override fun toString(): String {
        return "AccountPropertiesResponse(pk=$pk, email='$email', username='$username', detail='$detail')"
    }
}