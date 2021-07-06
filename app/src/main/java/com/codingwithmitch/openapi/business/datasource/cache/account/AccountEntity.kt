package com.codingwithmitch.openapi.business.datasource.cache.account

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.codingwithmitch.openapi.business.domain.models.Account

@Entity(tableName = "account_properties")
data class AccountEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "pk")
    val pk: Int,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "username")
    val username: String
)

fun AccountEntity.toAccount(): Account {
    return Account(
        pk = pk,
        email = email,
        username = username
    )
}

fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        pk = pk,
        email = email,
        username = username
    )
}