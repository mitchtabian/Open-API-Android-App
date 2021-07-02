package com.codingwithmitch.openapi.api.main

import com.codingwithmitch.openapi.models.Account
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AccountDto(

    @SerializedName("pk")
    @Expose
    val pk: Int,

    @SerializedName("email")
    val email: String,

    @SerializedName("username")
    val username: String
)

fun AccountDto.toAccount(): Account {
    return Account(
        pk = pk,
        email = email,
        username = username
    )
}









