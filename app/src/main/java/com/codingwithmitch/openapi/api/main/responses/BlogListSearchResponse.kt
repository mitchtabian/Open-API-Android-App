package com.codingwithmitch.openapi.api.main.responses

import com.codingwithmitch.openapi.api.main.BlogPostDto
import com.codingwithmitch.openapi.api.main.toBlogPost
import com.codingwithmitch.openapi.models.BlogPost
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Class for modeling the response when querying https://open-api.xyz/
 * See example response here: https://gist.github.com/mitchtabian/ae03573737067c9269701ea662460205
 */
class BlogListSearchResponse(

    @SerializedName("results")
    @Expose
    var results: List<BlogPostDto>,

    @SerializedName("detail")
    @Expose
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






