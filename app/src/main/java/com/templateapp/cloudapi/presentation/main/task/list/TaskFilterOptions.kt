package com.templateapp.cloudapi.presentation.main.task.list

enum class TaskFilterOptions(val value: String) {
    USERNAME("username"),
    DATE_CREATED("createdAt"),
    DATE_UPDATED("updatedAt"),
}

fun getFilterFromValue(value: String?): TaskFilterOptions{
    return when(value){
        TaskFilterOptions.USERNAME.value -> TaskFilterOptions.USERNAME
        TaskFilterOptions.DATE_CREATED.value -> TaskFilterOptions.DATE_CREATED
        TaskFilterOptions.DATE_UPDATED.value -> TaskFilterOptions.DATE_UPDATED
        else -> TaskFilterOptions.DATE_CREATED
    }
}