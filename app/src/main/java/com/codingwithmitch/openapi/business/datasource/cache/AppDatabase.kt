package com.codingwithmitch.openapi.business.datasource.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codingwithmitch.openapi.business.datasource.cache.account.AccountDao
import com.codingwithmitch.openapi.business.datasource.cache.account.AccountEntity
import com.codingwithmitch.openapi.business.datasource.cache.auth.AuthTokenDao
import com.codingwithmitch.openapi.business.datasource.cache.auth.AuthTokenEntity
import com.codingwithmitch.openapi.business.datasource.cache.blog.BlogPostDao
import com.codingwithmitch.openapi.business.datasource.cache.blog.BlogPostEntity

@Database(entities = [AuthTokenEntity::class, AccountEntity::class, BlogPostEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getAuthTokenDao(): AuthTokenDao

    abstract fun getAccountPropertiesDao(): AccountDao

    abstract fun getBlogPostDao(): BlogPostDao

    companion object{
        val DATABASE_NAME: String = "app_db"
    }


}








