package com.codingwithmitch.openapi.business.domain.models

data class BlogPost(
    val pk: Int,
    val title: String,
    val slug: String,
    val body: String,
    val image: String,
    val date_updated: Long,
    val username: String
)
