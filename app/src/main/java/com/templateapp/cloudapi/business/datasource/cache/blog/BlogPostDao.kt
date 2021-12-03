package com.templateapp.cloudapi.business.datasource.cache.blog

import androidx.room.*
import com.templateapp.cloudapi.business.domain.util.Constants.Companion.PAGINATION_PAGE_SIZE

@Dao
interface BlogPostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blogPost: BlogPostEntity): Long

    @Delete
    suspend fun deleteBlogPost(blogPost: BlogPostEntity)

    @Query("DELETE FROM blog_post WHERE id = :id")
    suspend fun deleteBlogPost(id: String)

    @Query("""
        UPDATE blog_post SET completed =:completed, title = :title, description = :description, image = :image, createdAt =:createdAt, updatedAt =:updatedAt
        WHERE id = :id
        """)

    suspend fun updateBlogPost(id: String, completed: Boolean, title: String, description: String, image: String, createdAt: Long, updatedAt: Long)

    @Query("""
        SELECT * FROM blog_post 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR username LIKE '%' || :query || '%' 
        LIMIT (:page * :pageSize)
        """)
    suspend fun getAllBlogPosts(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<BlogPostEntity>

    @Query("""
        SELECT * FROM blog_post 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR username LIKE '%' || :query || '%' 
        ORDER BY createdAt DESC LIMIT (:page * :pageSize)
        """)
    suspend fun searchBlogPostsOrderByDateCreatedDESC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<BlogPostEntity>

    @Query("""
        SELECT * FROM blog_post 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR username LIKE '%' || :query || '%' 
        ORDER BY createdAt ASC LIMIT (:page * :pageSize)""")
    suspend fun searchBlogPostsOrderByDateCreatedASC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<BlogPostEntity>

    @Query("""
        SELECT * FROM blog_post 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR username LIKE '%' || :query || '%' 
        ORDER BY updatedAt DESC LIMIT (:page * :pageSize)
        """)
    suspend fun searchBlogPostsOrderByDateUpdatedDESC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<BlogPostEntity>

    @Query("""
        SELECT * FROM blog_post 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR username LIKE '%' || :query || '%' 
        ORDER BY updatedAt ASC LIMIT (:page * :pageSize)""")
    suspend fun searchBlogPostsOrderByDateUpdatedASC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<BlogPostEntity>

    @Query("""
        SELECT * FROM blog_post 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR username LIKE '%' || :query || '%' 
        ORDER BY username DESC LIMIT (:page * :pageSize)""")
    suspend fun searchBlogPostsOrderByAuthorDESC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<BlogPostEntity>

    @Query("""
        SELECT * FROM blog_post 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR username LIKE '%' || :query || '%' 
        ORDER BY username  ASC LIMIT (:page * :pageSize)
        """)
    suspend fun searchBlogPostsOrderByAuthorASC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<BlogPostEntity>

    @Query("SELECT * FROM blog_post WHERE id = :id")
    suspend fun getBlogPost(id: String): BlogPostEntity?
}

suspend fun BlogPostDao.returnOrderedBlogQuery(
    query: String,
    filterAndOrder: String,
    page: Int
): List<BlogPostEntity> {

    when{
        filterAndOrder.contains(BlogQueryUtils.ORDER_BY_DESC_DATE_CREATED) ->{
            return searchBlogPostsOrderByDateCreatedDESC(
                query = query,
                page = page)
        }

        filterAndOrder.contains(BlogQueryUtils.ORDER_BY_ASC_DATE_CREATED) ->{
            return searchBlogPostsOrderByDateCreatedASC(
                query = query,
                page = page)
        }

        filterAndOrder.contains(BlogQueryUtils.ORDER_BY_DESC_DATE_UPDATED) ->{
            return searchBlogPostsOrderByDateUpdatedDESC(
                query = query,
                page = page)
        }

        filterAndOrder.contains(BlogQueryUtils.ORDER_BY_ASC_DATE_UPDATED) ->{
            return searchBlogPostsOrderByDateUpdatedASC(
                query = query,
                page = page)
        }

        filterAndOrder.contains(BlogQueryUtils.ORDER_BY_DESC_USERNAME) ->{
            return searchBlogPostsOrderByAuthorDESC(
                query = query,
                page = page)
        }

        filterAndOrder.contains(BlogQueryUtils.ORDER_BY_ASC_USERNAME) ->{
            return searchBlogPostsOrderByAuthorASC(
                query = query,
                page = page)
        }
        else ->
            return searchBlogPostsOrderByDateCreatedASC(
                query = query,
                page = page
            )
    }
}











