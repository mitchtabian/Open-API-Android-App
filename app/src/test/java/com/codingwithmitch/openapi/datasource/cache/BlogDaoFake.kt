package com.codingwithmitch.openapi.datasource.cache

import com.codingwithmitch.openapi.business.datasource.cache.blog.BlogPostDao
import com.codingwithmitch.openapi.business.datasource.cache.blog.BlogPostEntity

class BlogDaoFake(
    private val db: AppDatabaseFake
): BlogPostDao {
    override suspend fun insert(blogPost: BlogPostEntity): Long {
        db.blogs.removeIf {
            it.pk == blogPost.pk
        }
        db.blogs.add(blogPost)
        return 1 // always return success
    }

    override suspend fun deleteBlogPost(blogPost: BlogPostEntity) {
        db.blogs.remove(blogPost)
    }

    override suspend fun deleteBlogPost(pk: Int) {
        for(blog in db.blogs){
            if(blog.pk == pk){
                db.blogs.remove(blog)
                break
            }
        }
    }

    override suspend fun updateBlogPost(pk: Int, title: String, body: String, image: String) {
        for(blog in db.blogs){
            if(blog.pk == pk){
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

    override suspend fun searchBlogPostsOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<BlogPostEntity> {
        val copy = db.blogs.toMutableList()
        copy.sortByDescending { it.date_updated }
        return copy
    }

    override suspend fun searchBlogPostsOrderByDateASC(
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

    override suspend fun getBlogPost(pk: Int): BlogPostEntity? {
        for(blog in db.blogs){
            if(blog.pk == pk){
                return blog
            }
        }
        return null
    }
}