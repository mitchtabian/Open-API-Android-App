package com.templateapp.cloudapi.presentation.main.blog.list

enum class BlogOrderOptions(val value: String) {
    ASC(""),
    DESC("-")
}

fun getOrderFromValue(value: String?): BlogOrderOptions {
    return when(value){
        BlogOrderOptions.ASC.value -> BlogOrderOptions.ASC
        BlogOrderOptions.DESC.value -> BlogOrderOptions.DESC
        else -> BlogOrderOptions.DESC
    }
}