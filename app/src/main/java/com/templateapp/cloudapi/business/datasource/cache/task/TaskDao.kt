package com.templateapp.cloudapi.business.datasource.cache.task

import androidx.room.*
import com.templateapp.cloudapi.business.domain.util.Constants.Companion.PAGINATION_PAGE_SIZE

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM task WHERE id = :id")
    suspend fun deleteTask(id: String)

    @Query("""
        UPDATE task SET completed =:completed, title = :title, description = :description, image = :image, createdAt =:createdAt, updatedAt =:updatedAt
        WHERE id = :id
        """)

    suspend fun updateTask(id: String, completed: Boolean, title: String, description: String, image: String, createdAt: Long, updatedAt: Long)

    @Query("""
        SELECT * FROM task 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR username LIKE '%' || :query || '%' 
        LIMIT (:page * :pageSize)
        """)
    suspend fun getAllTasks(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<TaskEntity>

    @Query("""
        SELECT * FROM task 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR username LIKE '%' || :query || '%' 
        ORDER BY createdAt DESC LIMIT (:page * :pageSize)
        """)
    suspend fun searchTasksOrderByDateCreatedDESC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<TaskEntity>

    @Query("""
        SELECT * FROM task 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR username LIKE '%' || :query || '%' 
        ORDER BY createdAt ASC LIMIT (:page * :pageSize)""")
    suspend fun searchTasksOrderByDateCreatedASC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<TaskEntity>

    @Query("""
        SELECT * FROM task 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR username LIKE '%' || :query || '%' 
        ORDER BY updatedAt DESC LIMIT (:page * :pageSize)
        """)
    suspend fun searchTasksOrderByDateUpdatedDESC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<TaskEntity>

    @Query("""
        SELECT * FROM task 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR username LIKE '%' || :query || '%' 
        ORDER BY updatedAt ASC LIMIT (:page * :pageSize)""")
    suspend fun searchTasksOrderByDateUpdatedASC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<TaskEntity>

    @Query("""
        SELECT * FROM task 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR username LIKE '%' || :query || '%' 
        ORDER BY username DESC LIMIT (:page * :pageSize)""")
    suspend fun searchTasksOrderByAuthorDESC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<TaskEntity>

    @Query("""
        SELECT * FROM task 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        OR username LIKE '%' || :query || '%' 
        ORDER BY username  ASC LIMIT (:page * :pageSize)
        """)
    suspend fun searchTasksOrderByAuthorASC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<TaskEntity>

    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getTask(id: String): TaskEntity?
}

suspend fun TaskDao.returnOrderedTaskQuery(
    query: String,
    filterAndOrder: String,
    page: Int
): List<TaskEntity> {

    when{
        filterAndOrder.contains(TaskQueryUtils.ORDER_BY_DESC_DATE_CREATED) ->{
            return searchTasksOrderByDateCreatedDESC(
                query = query,
                page = page)
        }

        filterAndOrder.contains(TaskQueryUtils.ORDER_BY_ASC_DATE_CREATED) ->{
            return searchTasksOrderByDateCreatedASC(
                query = query,
                page = page)
        }

        filterAndOrder.contains(TaskQueryUtils.ORDER_BY_DESC_DATE_UPDATED) ->{
            return searchTasksOrderByDateUpdatedDESC(
                query = query,
                page = page)
        }

        filterAndOrder.contains(TaskQueryUtils.ORDER_BY_ASC_DATE_UPDATED) ->{
            return searchTasksOrderByDateUpdatedASC(
                query = query,
                page = page)
        }

        filterAndOrder.contains(TaskQueryUtils.ORDER_BY_DESC_USERNAME) ->{
            return searchTasksOrderByAuthorDESC(
                query = query,
                page = page)
        }

        filterAndOrder.contains(TaskQueryUtils.ORDER_BY_ASC_USERNAME) ->{
            return searchTasksOrderByAuthorASC(
                query = query,
                page = page)
        }
        else ->
            return searchTasksOrderByDateCreatedASC(
                query = query,
                page = page
            )
    }
}











