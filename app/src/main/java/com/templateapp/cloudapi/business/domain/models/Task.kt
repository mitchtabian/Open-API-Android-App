package com.templateapp.cloudapi.business.domain.models

data class Task(
    val id: String,
    val completed: Boolean,
    val title: String,
    val description: String,
    val image: String,
    val createdAt: Long,
    val updatedAt: Long,
    val username: String
)