package com.templateapp.cloudapi.business.datasource.cache.task

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.templateapp.cloudapi.business.domain.models.Task

@Entity(tableName = "task")
data class TaskEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "completed")
    val completed: Boolean,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "image")
    val image: String,

    @ColumnInfo(name = "createdAt")
    val createdAt: Long,

    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long,

    @ColumnInfo(name = "username")
    val username: String
)

fun TaskEntity.toTask(): Task{
    return Task(
        id = id,
        completed = completed,
        title = title,
        description = description,
        image = image,
        createdAt = createdAt,
        updatedAt = updatedAt,
        username = username
    )
}

fun Task.toEntity(): TaskEntity{
    return TaskEntity(
        id = id,
        completed = completed,
        title = title,
        description = description,
        image = image,
        createdAt = createdAt,
        updatedAt = updatedAt,
        username = username
    )
}











