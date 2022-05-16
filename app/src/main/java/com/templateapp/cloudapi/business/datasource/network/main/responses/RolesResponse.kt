package com.templateapp.cloudapi.business.datasource.network.main.responses

import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.cache.account.toEntity
import com.templateapp.cloudapi.business.domain.models.Role

class RolesResponse (
    @SerializedName("roles")
    var roles: List<RoleDto>,

    @SerializedName("count")
    var count: Int
)


fun RolesResponse.toList(): List<Role>{
    val list: MutableList<Role> = mutableListOf()
    for(dto in roles){
        list.add(
            dto.toRole()
        )
    }
    return list
}

