package com.templateapp.cloudapi.business.datasource.cache.account

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.templateapp.cloudapi.business.domain.models.Account

@Entity(tableName = "account_properties")
data class AccountEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "_id")
    val _id: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "name")
    val username: String
)

fun AccountEntity.toAccount(): Account {
    return Account(
        id = _id,
        email = email,
        username = username
    )
}

fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        _id = id,
        email = email,
        username = username
    )
}