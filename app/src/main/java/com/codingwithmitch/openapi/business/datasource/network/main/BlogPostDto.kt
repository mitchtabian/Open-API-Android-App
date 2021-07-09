package com.codingwithmitch.openapi.business.datasource.network.main

import com.codingwithmitch.openapi.business.domain.models.BlogPost
import com.codingwithmitch.openapi.business.domain.util.DateUtils
import com.google.gson.annotations.SerializedName

class BlogPostDto(

    @SerializedName("pk")
    val pk: Int,

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
            pk = pk,
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
        pk = pk,
        title = title,
        slug = slug,
        body = body,
        image = image,
        date_updated = DateUtils.convertLongToStringDate(dateUpdated),
        username = username
    )
}












