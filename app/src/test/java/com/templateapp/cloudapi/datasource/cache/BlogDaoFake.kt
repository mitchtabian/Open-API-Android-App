package com.templateapp.cloudapi.datasource.cache

import com.templateapp.cloudapi.business.datasource.cache.blog.BlogPostDao
import com.templateapp.cloudapi.business.datasource.cache.blog.BlogPostEntity

class BlogDaoFake(
    private val db: AppDatabaseFake
): BlogPostDao {
    override suspend fun insert(blogPost: BlogPostEntity): Long {
        db.blogs.removeIf {
            it.id == blogPost.id
        }
        db.blogs.add(blogPost)
        return 1 // always return success
    }

    override suspend fun deleteBlogPost(blogPost: BlogPostEntity) {
        db.blogs.remove(blogPost)
    }

    override suspend fun deleteBlogPost(id: String) {
        for(blog in db.blogs){
            if(blog.id.equals(id)){
                db.blogs.remove(blog)
                break
            }
        }
    }

    override suspend fun updateBlogPost(id: String, title: String, body: String, image: String) {
        for(blog in db.blogs){
            if(blog.id.equals(id)){
                db.blogs.remove(blog)
                val updated = blog.copy(title = title, body = body, image = image)
                db.blogs.add(updated)
                break
            }
        }
    }

    override suspend fun getAllBlogPosts(
        query: String,
        page: Int,
        pageSize: Int
    ): List<BlogPostEntity> {
        return db.blogs
    }

    override suspend fun searchBlogPostsOrderByDateCreatedDESC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<BlogPostEntity> {
        val copy = db.blogs.toMutableList()
        copy.sortByDescending { it.date_updated }
        return copy
    }

    override suspend fun searchBlogPostsOrderByDateCreatedASC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<BlogPostEntity> {
        val copy = db.blogs.toMutableList()
        copy.sortByDescending { it.date_updated }
        copy.reverse()
        return copy
    }

    override suspend fun searchBlogPostsOrderByAuthorDESC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<BlogPostEntity> {
        val copy = db.blogs.toMutableList()
        copy.sortByDescending { it.username }
        return copy
    }

    override suspend fun searchBlogPostsOrderByAuthorASC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<BlogPostEntity> {
        val copy = db.blogs.toMutableList()
        copy.sortByDescending { it.username }
        copy.reverse()
        return copy
    }

    override suspend fun getBlogPost(id: String): BlogPostEntity? {
        for(blog in db.blogs){
            if(blog.id.equals(id)){
                return blog
            }
        }
        return null
    }
}