package com.codingwithmitch.openapi.business.datasource.network.main.responses

import com.codingwithmitch.openapi.business.domain.models.BlogPost
import com.codingwithmitch.openapi.business.domain.util.DateUtils
import com.google.gson.annotations.SerializedName

class BlogCreateUpdateResponse(

    @SerializedName("response")
    val response: String,

    @SerializedName("error_message")
    val errorMessage: String,

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

fun BlogCreateUpdateResponse.toBlogPost(): BlogPost {
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













