package com.templateapp.cloudapi.business.datasource.network.main

import com.templateapp.cloudapi.business.domain.models.BlogPost
import com.templateapp.cloudapi.business.domain.util.DateUtils
import com.google.gson.annotations.SerializedName

class BlogPostDto(

    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("slug")
    val slug: String,

    @SerializedName("body")
    val body: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("date_updated")
    val date_updated: String,

    @SerializedName("username")
    val username: String


)

fun BlogPostDto.toBlogPost(): BlogPost{
    return BlogPost(
            id = id,
            title = title,
            slug = slug,
            body = body,
            image = image,
            dateUpdated = DateUtils.convertServerStringDateToLong(
                date_updated
            ),
            username = username
        )
}


fun BlogPost.toDto(): BlogPostDto {
    return BlogPostDto(
        id = id,
        title = title,
        slug = slug,
        body = body,
        image = image,
        date_updated = DateUtils.convertLongToStringDate(dateUpdated),
        username = username
    )
}












