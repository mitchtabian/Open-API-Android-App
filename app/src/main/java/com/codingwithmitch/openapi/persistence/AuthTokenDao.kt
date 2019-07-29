package com.codingwithmitch.openapi.persistence

import androidx.room.*
import com.codingwithmitch.openapi.models.AuthToken

@Dao
interface AuthTokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(authToken: AuthToken): Long

    @Delete
    suspend fun delete(authToken: AuthToken): Int

    @Query("DELETE FROM auth_token")
    suspend fun deleteAll(): Int

    @Query("SELECT * FROM auth_token WHERE token = :token")
    suspend fun searchByToken(token: String): AuthToken

    @Query("SELECT * FROM auth_token WHERE account_pk = :pk")
    suspend fun searchByPk(pk: Int): AuthToken

    @Query("SELECT * FROM auth_token")
    suspend fun selectAll(): List<AuthToken>

    @Query("UPDATE auth_token SET token = null WHERE account_pk = :pk")
    suspend fun updateToken(pk: Int): Int
}
















