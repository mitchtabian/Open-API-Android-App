package com.codingwithmitch.openapi.api.main.responses

import com.codingwithmitch.openapi.api.main.BlogPostDto
import com.codingwithmitch.openapi.api.main.toBlogPost
import com.codingwithmitch.openapi.models.BlogPost
import com.google.gson.annotations.SerializedName

class BlogListSearchResponse(

    @SerializedName("results")
    var results: List<BlogPostDto>,

    @SerializedName("detail")
    var detail: String
)

fun BlogListSearchResponse.toList(): List<BlogPost>{
    val list: MutableList<BlogPost> = mutableListOf()
    for(dto in results){
        list.add(
            dto.toBlogPost()
        )
    }
    return list
}






