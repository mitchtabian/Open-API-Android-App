package com.codingwithmitch.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Account")
data class Account(
    @PrimaryKey(autoGenerate = false) var pk: Int,
    @ColumnInfo(name = "email") var email: String,


    ) {

}