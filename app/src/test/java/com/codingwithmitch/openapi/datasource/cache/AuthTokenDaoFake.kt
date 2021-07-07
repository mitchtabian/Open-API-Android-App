package com.codingwithmitch.openapi.datasource.cache

import com.codingwithmitch.openapi.business.datasource.cache.auth.AuthTokenDao
import com.codingwithmitch.openapi.business.datasource.cache.auth.AuthTokenEntity

class AuthTokenDaoFake(
    private val db: AppDatabaseFake
): AuthTokenDao {

    override suspend fun insert(authToken: AuthTokenEntity): Long {
        db.authTokens.add(authToken)
        return 1 // always return success
    }

    override suspend fun clearTokens() {
        db.authTokens.clear()
    }

    override suspend fun searchByPk(pk: Int): AuthTokenEntity? {
        for(entity in db.authTokens){
            if(entity.account_pk == pk){
                return entity
            }
        }
        return null
    }
}