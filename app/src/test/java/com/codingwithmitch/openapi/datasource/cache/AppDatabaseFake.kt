package com.codingwithmitch.openapi.datasource.cache

import com.codingwithmitch.openapi.business.datasource.cache.account.AccountEntity
import com.codingwithmitch.openapi.business.datasource.cache.auth.AuthTokenEntity
import com.codingwithmitch.openapi.business.datasource.cache.blog.BlogPostEntity

class AppDatabaseFake {

    // fake db tables
    val blogs = mutableListOf<BlogPostEntity>()
    val accounts = mutableListOf<AccountEntity>()
    val authTokens = mutableListOf<AuthTokenEntity>()

}