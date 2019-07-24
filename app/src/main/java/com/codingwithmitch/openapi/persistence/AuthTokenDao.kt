package com.codingwithmitch.openapi.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codingwithmitch.openapi.models.AuthToken
import retrofit2.http.DELETE

@Dao
abstract class AuthTokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg authToken: AuthToken): Long

    @DELETE
    abstract fun delete(vararg authToken: AuthToken): Int

    @Query("DELETE FROM auth_token")
    abstract fun deleteAll(): Int

    @Query("SELECT * FROM auth_token WHERE token = :token")
    abstract fun searchByToken(token: String)

    @Query("SELECT * FROM auth_token WHERE account_pk = :pk")
    abstract fun searchByPk(pk: Int)


}
















