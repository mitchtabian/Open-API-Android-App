package com.templateapp.cloudapi.datasource.cache

import com.templateapp.cloudapi.business.datasource.cache.account.AccountEntity
import com.templateapp.cloudapi.business.datasource.cache.auth.AuthTokenEntity
import com.templateapp.cloudapi.business.datasource.cache.blog.BlogPostEntity

class AppDatabaseFake {

    // fake db tables
    val blogs = mutableListOf<BlogPostEntity>()
    val accounts = mutableListOf<AccountEntity>()
    val authTokens = mutableListOf<AuthTokenEntity>()

}