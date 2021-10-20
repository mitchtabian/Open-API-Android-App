package com.templateapp.cloudapi.business.datasource.cache.auth

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.datasource.cache.account.AccountEntity
import com.google.gson.annotations.Expose

@Entity(
    tableName = "auth_token",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["_id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AuthTokenEntity(

    @PrimaryKey
    @ColumnInfo(name = "account_id")
    var account_id: String = "-1",


    @ColumnInfo(name = "token")
    @Expose
    val token: String? = null
)

fun AuthTokenEntity.toAuthToken(): AuthToken {
    if(account_id == null){
        throw Exception("Account _id cannot be null.")
    }
    if(token == null){
        throw Exception("Token cannot be null.")
    }
    return AuthToken(
        accountId = account_id!!,
        token = token,
    )
}

fun AuthToken.toEntity(): AuthTokenEntity{
    return AuthTokenEntity(
        account_id = accountId,
        token = token,
    )
}

