package com.codingwithmitch.openapi.models

data class BlogPost(
    val pk: Int,
    val title: String,
    val slug: String,
    val body: String,
    val image: String,
    val date_updated: Long,
    val username: String
)
