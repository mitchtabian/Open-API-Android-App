package com.templateapp.cloudapi.business.domain.models

data class BlogPost(
    val id: String,
    val title: String,
    val slug: String,
    val body: String,
    val image: String,
    val dateUpdated: Long,
    val username: String
)