package com.templateapp.cloudapi.business.datasource.network.main

import com.templateapp.cloudapi.business.domain.models.Account
import com.google.gson.annotations.SerializedName

class AccountDto(

    @SerializedName("_id")
    val _id: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("username")
    val username: String
)

fun AccountDto.toAccount(): Account {
    return Account(
        id = _id,
        email = email,
        username = username
    )
}









