package com.codingwithmitch.openapi.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local data class for modeling: https://open-api.xyz/api/account/properties
 * NOTES:
 * 1) local 'auth_token' table has foreign key relationship to 'account_properties' table
 * 2) pk of 'account_properties' matches the pk on server (open-api.xyz)
 *
 * Docs: https://open-api.xyz/api/
 */
@Entity(tableName = "account_properties")
data class AccountProperties(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "pk") var pk: Int,
    @ColumnInfo(name = "email") var email: String,
    @ColumnInfo(name = "username") var username: String
)
{

}





















