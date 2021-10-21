package com.templateapp.cloudapi.business.datasource.cache.blog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.templateapp.cloudapi.business.domain.models.BlogPost

@Entity(tableName = "blog_post")
data class BlogPostEntity(

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

fun BlogPostEntity.toBlogPost(): BlogPost{
    return BlogPost(
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

fun BlogPost.toEntity(): BlogPostEntity{
    return BlogPostEntity(
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











