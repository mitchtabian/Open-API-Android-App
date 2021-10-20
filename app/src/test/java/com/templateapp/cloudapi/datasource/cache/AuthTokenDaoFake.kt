package com.templateapp.cloudapi.datasource.cache

import com.templateapp.cloudapi.business.datasource.cache.auth.AuthTokenDao
import com.templateapp.cloudapi.business.datasource.cache.auth.AuthTokenEntity

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

    override suspend fun searchById(id: String): AuthTokenEntity? {
        for(entity in db.authTokens){
            if(entity.account_id.equals(id)){
                return entity
            }
        }
        return null
    }
}