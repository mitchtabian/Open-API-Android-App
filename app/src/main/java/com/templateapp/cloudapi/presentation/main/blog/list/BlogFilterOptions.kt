package com.templateapp.cloudapi.presentation.main.blog.list

enum class BlogFilterOptions(val value: String) {
    USERNAME("username"),
    DATE_CREATED("createdAt"),
}

fun getFilterFromValue(value: String?): BlogFilterOptions{
    return when(value){
        BlogFilterOptions.USERNAME.value -> BlogFilterOptions.USERNAME
        BlogFilterOptions.DATE_CREATED.value -> BlogFilterOptions.DATE_CREATED
        else -> BlogFilterOptions.DATE_CREATED
    }
}