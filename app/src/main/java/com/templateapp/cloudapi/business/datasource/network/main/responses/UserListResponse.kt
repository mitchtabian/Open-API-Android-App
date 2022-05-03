package com.templateapp.cloudapi.business.datasource.network.main.responses

import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.network.main.AccountDto
import com.templateapp.cloudapi.business.datasource.network.main.toAccount
import com.templateapp.cloudapi.business.domain.models.Account

class UserListResponse (
    @SerializedName("results")
    var results: List<AccountDto>
)


fun UserListResponse.toList(): List<Account>{
    val list: MutableList<Account> = mutableListOf()
    for(dto in results){
        list.add(
            dto.toAccount()
        )
    }
    return list
}

