package com.templateapp.cloudapi.business.datasource.network.main

import com.templateapp.cloudapi.business.domain.models.BlogPost
import com.templateapp.cloudapi.business.domain.util.DateUtils
import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.network.responseObjects.User

class BlogPostDto(

    @SerializedName("completed")
    val completed: Boolean,

    @SerializedName("_id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String,

    @SerializedName("owner")
    val owner: User,

    @SerializedName("error")
    val error: String?,
)

fun BlogPostDto.toBlogPost(): BlogPost{
    return BlogPost(
            id = id,
            completed = completed,
            title = title,
            description = description,
            image = image,
            createdAt = DateUtils.convertServerStringDateToLong(createdAt),
            updatedAt = DateUtils.convertServerStringDateToLong(updatedAt),
            username = owner.name
        )
}

/*
fun BlogPost.toDto(): BlogPostDto {
    return BlogPostDto(
        id = id,
        completed = completed,
        title = title,
        description = description,
        image = image,
        updatedAt = DateUtils.convertLongToStringDate(updatedAt),
        createdAt = DateUtils.convertLongToStringDate(createdAt),
        username = owner.name
    )
}
*/
