package com.codingwithmitch.openapi.presentation.main.blog.list

enum class BlogFilterOptions(val value: String) {
    USERNAME("username"),
    DATE_UPDATED("date_updated"),
}

fun getFilterFromValue(value: String?): BlogFilterOptions{
    return when(value){
        BlogFilterOptions.USERNAME.value -> BlogFilterOptions.USERNAME
        BlogFilterOptions.DATE_UPDATED.value -> BlogFilterOptions.DATE_UPDATED
        else -> BlogFilterOptions.DATE_UPDATED
    }
}