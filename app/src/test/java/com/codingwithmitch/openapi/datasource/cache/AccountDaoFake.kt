package com.codingwithmitch.openapi.datasource.cache

import com.codingwithmitch.openapi.business.datasource.cache.account.AccountDao
import com.codingwithmitch.openapi.business.datasource.cache.account.AccountEntity

class AccountDaoFake(
    private val db: AppDatabaseFake
): AccountDao {

    override suspend fun searchByEmail(email: String): AccountEntity? {
        for(account in db.accounts){
            if(account.email == email){
                return account
            }
        }
        return null
    }

    override suspend fun searchByPk(pk: Int): AccountEntity? {
        for(account in db.accounts){
            if(account.pk == pk){
                return account
            }
        }
        return null
    }

    override suspend fun insertAndReplace(account: AccountEntity): Long {
        db.accounts.removeIf {
            it.pk == account.pk
        }
        db.accounts.add(account)
        return 1 // always return success
    }

    override suspend fun insertOrIgnore(account: AccountEntity): Long {
        if(!db.accounts.contains(account)){
            db.accounts.add(account)
        }
        return 1 // always return success
    }

    override suspend fun updateAccount(pk: Int, email: String, username: String) {
        for(account in db.accounts){
            if(account.pk == pk){
                val updated = account.copy(email = email, username = username)
                db.accounts.remove(account)
                db.accounts.add(updated)
                break
            }
        }
    }
}




