package com.templateapp.cloudapi.presentation.main.blog.list

enum class BlogFilterOptions(val value: String) {
    USERNAME("username"),
    DATE_UPDATED("modifiedAt"),
}

fun getFilterFromValue(value: String?): BlogFilterOptions{
    return when(value){
        BlogFilterOptions.USERNAME.value -> BlogFilterOptions.USERNAME
        BlogFilterOptions.DATE_UPDATED.value -> BlogFilterOptions.DATE_UPDATED
        else -> BlogFilterOptions.DATE_UPDATED
    }
}