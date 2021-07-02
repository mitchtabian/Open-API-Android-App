package com.codingwithmitch.openapi.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Data class for saving authentication token locally for open-api.xyz
 * NOTES:
 * 1) local 'auth_token' table has foreign key relationship to 'account_properties' table through 'account' field (PK)
 *
 * Docs: https://open-api.xyz/api/
 */

const val AUTH_TOKEN_BUNDLE_KEY = "com.codingwithmitch.openapi.models.AuthToken"

@Entity(
    tableName = "auth_token",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["pk"],
            childColumns = ["account_pk"],
            onDelete = CASCADE
        )
    ]
)
@Parcelize
data class AuthToken(

    @PrimaryKey
    @ColumnInfo(name = "account_pk")
    var account_pk: Int? = -1,


    @ColumnInfo(name = "token")
    @SerializedName("token")
    @Expose
    var token: String? = null
) : Parcelable













