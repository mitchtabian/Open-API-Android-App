package com.codingwithmitch.openapi.api.auth.network_responses

import com.google.gson.annotations.SerializedName

class RegistrationResponse(

    @SerializedName("response")
    var response: String,

    @SerializedName("error_message")
    var errorMessage: String,

    @SerializedName("email")
    var email: String,

    @SerializedName("username")
    var username: String,

    @SerializedName("pk")
    var pk: Int,

    @SerializedName("token")
    var token: String)
{

    override fun toString(): String {
        return "RegistrationResponse(response='$response', errorMessage='$errorMessage', email='$email', username='$username', token='$token')"
    }
}