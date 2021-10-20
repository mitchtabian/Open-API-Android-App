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

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "slug")
    val slug: String,

    @ColumnInfo(name = "body")
    val body: String,

    @ColumnInfo(name = "image")
    val image: String,

    @ColumnInfo(name = "date_updated")
    val date_updated: Long,

    @ColumnInfo(name = "username")
    val username: String
)

fun BlogPostEntity.toBlogPost(): BlogPost{
    return BlogPost(
        id = id,
        title = title,
        slug = slug,
        body = body,
        image = image,
        dateUpdated = date_updated,
        username = username
    )
}

fun BlogPost.toEntity(): BlogPostEntity{
    return BlogPostEntity(
        id = id,
        title = title,
        slug = slug,
        body = body,
        image = image,
        date_updated = dateUpdated,
        username = username
    )
}











