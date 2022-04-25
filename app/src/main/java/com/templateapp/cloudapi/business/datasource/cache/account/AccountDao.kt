package com.templateapp.cloudapi.business.datasource.cache.account

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.templateapp.cloudapi.business.datasource.cache.task.TaskEntity
import com.templateapp.cloudapi.business.domain.util.Constants

@Dao
interface AccountDao {

    @Query("SELECT * FROM account_properties WHERE email = :email")
    suspend fun searchByEmail(email: String): AccountEntity?

    @Query("SELECT * FROM account_properties WHERE _id = :id")
    suspend fun searchByPk(id: String): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAndReplace(account: AccountEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(account: AccountEntity): Long

    @Query("UPDATE account_properties SET email = :email, name = :name WHERE _id = :id")
    suspend fun updateAccount(id: String, email: String, name: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: AccountEntity): Long

}


















