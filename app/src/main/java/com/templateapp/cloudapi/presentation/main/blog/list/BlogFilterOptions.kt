package com.templateapp.cloudapi.presentation.main.blog.list

enum class BlogFilterOptions(val value: String) {
    USERNAME("username"),
    DATE_CREATED("createdAt"),
    DATE_UPDATED("updatedAt"),
}

fun getFilterFromValue(value: String?): BlogFilterOptions{
    return when(value){
        BlogFilterOptions.USERNAME.value -> BlogFilterOptions.USERNAME
        BlogFilterOptions.DATE_CREATED.value -> BlogFilterOptions.DATE_CREATED
        BlogFilterOptions.DATE_UPDATED.value -> BlogFilterOptions.DATE_UPDATED
        else -> BlogFilterOptions.DATE_CREATED
    }
}