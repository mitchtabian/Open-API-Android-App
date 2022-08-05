package com.templateapp.cloudapi.business.datasource.network.main.responses

import androidx.room.TypeConverters
import com.templateapp.cloudapi.business.domain.models.Account
import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.cache.account.RoleEntity
import com.templateapp.cloudapi.business.domain.models.Role

class DeviceDto(

    @SerializedName("id")
    val id: String,

)


/*fun AccountDto.toAccount(): Account {
    return Account(
        _id = _id,
        email = email,
        name = name,
        age = age,
        createdAt = createdAt,
        updatedAt = updatedAt,
        userCreatedSequence = userCreatedSequence,
        __v = __v,
        role = role,
        enabled = enabled
    )
}*/










