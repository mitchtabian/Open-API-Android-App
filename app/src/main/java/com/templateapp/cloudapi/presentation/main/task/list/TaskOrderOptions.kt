package com.templateapp.cloudapi.presentation.main.task.list

enum class TaskOrderOptions(val value: String) {
    ASC(":asc"),
    DESC(":desc")
}

fun getOrderFromValue(value: String?): TaskOrderOptions {
    return when(value){
        TaskOrderOptions.ASC.value -> TaskOrderOptions.ASC
        TaskOrderOptions.DESC.value -> TaskOrderOptions.DESC
        else -> TaskOrderOptions.DESC
    }
}