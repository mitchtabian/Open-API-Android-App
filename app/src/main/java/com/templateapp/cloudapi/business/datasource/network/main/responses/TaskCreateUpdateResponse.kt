package com.templateapp.cloudapi.business.datasource.network.main.responses

import com.templateapp.cloudapi.business.domain.util.DateUtils
import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.network.responseObjects.Task

class TaskCreateUpdateResponse(

    @SerializedName("response")
    val response: String,

    @SerializedName("error")
    val error: String,

    @SerializedName("task")
    val task: Task,

    @SerializedName("owner")
    val owner: String,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String,
)



fun TaskCreateUpdateResponse.toTask(): com.templateapp.cloudapi.business.domain.models.Task {
    return com.templateapp.cloudapi.business.domain.models.Task(
        id = task._id,
        completed = task.completed,
        title = task.title,
        description = task.description,
        image = task.image,
        createdAt = DateUtils.convertServerStringDateToLong(
            task.createdAt
        ),
        updatedAt = DateUtils.convertServerStringDateToLong(
            task.updatedAt
        ),
        username = task.owner
    )
}













