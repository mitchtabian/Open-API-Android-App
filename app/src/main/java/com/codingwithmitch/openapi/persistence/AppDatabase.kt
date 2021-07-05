package com.codingwithmitch.openapi.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codingwithmitch.openapi.persistence.account.AccountDao
import com.codingwithmitch.openapi.persistence.account.AccountEntity
import com.codingwithmitch.openapi.persistence.auth.AuthTokenDao
import com.codingwithmitch.openapi.persistence.auth.AuthTokenEntity
import com.codingwithmitch.openapi.persistence.blog.BlogPostDao
import com.codingwithmitch.openapi.persistence.blog.BlogPostEntity

@Database(entities = [AuthTokenEntity::class, AccountEntity::class, BlogPostEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getAuthTokenDao(): AuthTokenDao

    abstract fun getAccountPropertiesDao(): AccountDao

    abstract fun getBlogPostDao(): BlogPostDao

    companion object{
        val DATABASE_NAME: String = "app_db"
    }


}








