package com.codingwithmitch.openapi.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codingwithmitch.openapi.models.BlogPost

@Dao
interface BlogPostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blogPost: BlogPost): Long
}



