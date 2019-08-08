package com.codingwithmitch.openapi.persistence

import androidx.room.*
import com.codingwithmitch.openapi.models.BlogPost
import androidx.lifecycle.LiveData

@Dao
interface BlogPostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blogPost: BlogPost): Long

    @Query("SELECT * FROM blog_post WHERE title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%' ORDER BY date_updated DESC LIMIT (:page * 10)")
    fun searchBlogPostsOrderByDateDESC(query: String, page: Int): LiveData<List<BlogPost>>

    @Query("SELECT * FROM blog_post WHERE title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%' ORDER BY date_updated  ASC LIMIT (:page * 10)")
    fun searchBlogPostsOrderByDateASC(query: String, page: Int): LiveData<List<BlogPost>>

    @Query("SELECT * FROM blog_post WHERE title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%' ORDER BY username DESC LIMIT (:page * 10)")
    fun searchBlogPostsOrderByAuthorDESC(query: String, page: Int): LiveData<List<BlogPost>>

    @Query("SELECT * FROM blog_post WHERE title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%' ORDER BY username  ASC LIMIT (:page * 10)")
    fun searchBlogPostsOrderByAuthorASC(query: String, page: Int): LiveData<List<BlogPost>>
}
















