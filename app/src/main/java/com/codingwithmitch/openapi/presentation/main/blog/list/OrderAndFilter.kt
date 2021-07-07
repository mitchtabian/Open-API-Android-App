package com.codingwithmitch.openapi.presentation.main.blog.list

data class OrderAndFilter(
    val order: BlogOrderOptions,
    val filter: BlogFilterOptions,
)
