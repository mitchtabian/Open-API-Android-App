package com.templateapp.cloudapi.datasource.cache

import com.templateapp.cloudapi.business.datasource.cache.account.AccountDao
import com.templateapp.cloudapi.business.datasource.cache.account.AccountEntity

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

    override suspend fun searchByPk(id: String): AccountEntity? {
        for(account in db.accounts){
            if(account._id == id){
                return account
            }
        }
        return null
    }

    override suspend fun insertAndReplace(account: AccountEntity): Long {
        db.accounts.removeIf {
            it._id == account._id
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

    override suspend fun updateAccount(id: String, email: String, username: String) {
        for(account in db.accounts){
            if(account._id == id){
                val updated = account.copy(email = email, username = username)
                db.accounts.remove(account)
                db.accounts.add(updated)
                break
            }
        }
    }
}




