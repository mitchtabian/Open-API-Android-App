package com.templateapp.cloudapi.business.datasource.network.main

import androidx.room.TypeConverters
import com.templateapp.cloudapi.business.domain.models.Account
import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.cache.account.RoleEntity
import com.templateapp.cloudapi.business.domain.models.Role

class AccountDto(

    @SerializedName("_id")
    val _id: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("age")
    val age: Int,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String,

    @SerializedName("userCreatedSequence")
    val userCreatedSequence: Int,

    @SerializedName("role")
    @TypeConverters(RoleEntity::class)
    val role: Role,

    @SerializedName("__v")
    val __v: Int

)

fun AccountDto.toAccount(): Account {
    return Account(
        _id = _id,
        email = email,
        name = name,
        age = age,
        createdAt = createdAt,
        updatedAt = updatedAt,
        userCreatedSequence = userCreatedSequence,
        __v = __v,
        role = role
    )
}










