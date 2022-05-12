package com.templateapp.cloudapi.business.datasource.network.main.responses

import androidx.room.TypeConverters
import com.templateapp.cloudapi.business.domain.models.Account
import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.cache.account.RoleEntity
import com.templateapp.cloudapi.business.domain.models.Role

class RoleDto(

    @SerializedName("_id")
    val _id: String,

    @SerializedName("title")
    val title: String,

)

fun RoleDto.toRole(): Role {
    return Role(
        _id = _id,
       title = title
    )
}










