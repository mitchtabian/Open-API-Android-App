package com.codingwithmitch.openapi.persistence.auth

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.codingwithmitch.openapi.models.Account
import com.codingwithmitch.openapi.models.AuthToken
import com.google.gson.annotations.Expose

@Entity(
    tableName = "auth_token",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["pk"],
            childColumns = ["account_pk"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AuthTokenEntity(

    @PrimaryKey
    @ColumnInfo(name = "account_pk")
    var account_pk: Int? = -1,


    @ColumnInfo(name = "token")
    @Expose
    val token: String? = null
)

fun AuthTokenEntity.toAuthToken(): AuthToken {
    if(account_pk == null){
        throw Exception("Account PK cannot be null.")
    }
    if(token == null){
        throw Exception("Token cannot be null.")
    }
    return AuthToken(
        accountPk = account_pk!!,
        token = token,
    )
}

fun AuthToken.toEntity(): AuthTokenEntity{
    return AuthTokenEntity(
        account_pk = accountPk,
        token = token,
    )
}

