package com.templateapp.cloudapi.datasource.cache

import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.datasource.cache.task.TaskEntity

class BlogDaoFake(
    private val db: AppDatabaseFake
): TaskDao {
    override suspend fun insert(task: TaskEntity): Long {
        db.blogs.removeIf {
            it.id == task.id
        }
        db.blogs.add(task)
        return 1 // always return success
    }

    override suspend fun deleteTask(task: TaskEntity) {
        db.blogs.remove(task)
    }

    override suspend fun deleteTask(id: String) {
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

    override suspend fun getAllTasks(
        query: String,
        page: Int,
        pageSize: Int
    ): List<TaskEntity> {
        return db.blogs
    }

    override suspend fun searchTasksOrderByDateCreatedDESC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<TaskEntity> {
        val copy = db.blogs.toMutableList()
        copy.sortByDescending { it.date_updated }
        return copy
    }

    override suspend fun searchTasksOrderByDateCreatedASC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<TaskEntity> {
        val copy = db.blogs.toMutableList()
        copy.sortByDescending { it.date_updated }
        copy.reverse()
        return copy
    }

    override suspend fun searchTasksOrderByAuthorDESC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<TaskEntity> {
        val copy = db.blogs.toMutableList()
        copy.sortByDescending { it.username }
        return copy
    }

    override suspend fun searchTasksOrderByAuthorASC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<TaskEntity> {
        val copy = db.blogs.toMutableList()
        copy.sortByDescending { it.username }
        copy.reverse()
        return copy
    }

    override suspend fun getTask(id: String): TaskEntity? {
        for(blog in db.blogs){
            if(blog.id.equals(id)){
                return blog
            }
        }
        return null
    }
}