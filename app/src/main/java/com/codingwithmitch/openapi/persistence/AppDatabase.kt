package com.codingwithmitch.openapi.persistence

import android.accounts.Account
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.codingwithmitch.openapi.models.AuthToken

@Database(entities = arrayOf(AuthToken::class, Account::class), version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getAuthTokenDao(): AuthTokenDao

//    abstract fun getAccountDao(): AccountDao

    companion object{
        val DATABASE_NAME: String = "app_db"
    }


}