package com.codingwithmitch.openapi.api.main.responses

import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.util.DateUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BlogCreateUpdateResponse(

    @SerializedName("response")
    @Expose
    val response: String,

    @SerializedName("pk")
    @Expose
    val pk: Int,

    @SerializedName("title")
    @Expose
    val title: String,

    @SerializedName("slug")
    @Expose
    val slug: String,

    @SerializedName("body")
    @Expose
    val body: String,

    @SerializedName("image")
    @Expose
    val image: String,

    @SerializedName("date_updated")
    @Expose
    val date_updated: String,

    @SerializedName("username")
    @Expose
    val username: String

)

fun BlogCreateUpdateResponse.toBlogPost(): BlogPost {
    return BlogPost(
        pk = pk,
        title = title,
        slug = slug,
        body = body,
        image = image,
        date_updated = DateUtils.convertServerStringDateToLong(
            date_updated
        ),
        username = username
    )
}













