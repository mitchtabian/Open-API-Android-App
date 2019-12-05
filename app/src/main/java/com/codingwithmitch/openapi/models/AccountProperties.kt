package com.codingwithmitch.openapi.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "account_properties")
data class AccountProperties(

    @SerializedName("pk")
    @Expose
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "pk") var pk: Int,

    @SerializedName("email")
    @Expose
    @ColumnInfo(name = "email") var email: String,

    @SerializedName("username")
    @Expose
    @ColumnInfo(name = "username") var username: String
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false

        other as AccountProperties

        if (pk != other.pk) return false
        if (email != other.email) return false
        if (username != other.username) return false

        return true
    }

}











