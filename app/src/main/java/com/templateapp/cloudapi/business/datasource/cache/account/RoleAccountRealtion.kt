package com.templateapp.cloudapi.business.datasource.cache.account

import androidx.room.Embedded
import androidx.room.Relation
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.Role

data class RoleAccountRealtion(@Embedded val role: RoleEntity,
                               @Relation(
        parentColumn = "role",
        entityColumn = "title"
    )
    val account: List<AccountEntity>)
