package com.templateapp.cloudapi.business.datasource.cache.account

import android.accounts.Account
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.datasource.cache.task.TaskEntity
import com.templateapp.cloudapi.business.datasource.cache.task.TaskQueryUtils
import com.templateapp.cloudapi.business.domain.models.Role
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

    @Query("UPDATE account_properties SET email = :email, name = :name, age =:age, enabled =:enabled, role =:role WHERE _id = :id")
    suspend fun changeAccount(id: String, email: String, name: String, age: Int, enabled: Boolean, role: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: AccountEntity): Long

    @Query("DELETE FROM account_properties WHERE _id = :id")
    suspend fun deleteAccount(id: String)

    @Query("""
        SELECT * FROM account_properties
        LIMIT (:page * :pageSize)
        """)
    suspend fun getAllAccounts(
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): List<AccountEntity>


}


















