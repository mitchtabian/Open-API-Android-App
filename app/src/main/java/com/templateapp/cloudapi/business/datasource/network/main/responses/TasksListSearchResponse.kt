package com.templateapp.cloudapi.business.datasource.network.main.responses

import com.templateapp.cloudapi.business.datasource.network.main.TaskDto
import com.templateapp.cloudapi.business.datasource.network.main.toTask
import com.templateapp.cloudapi.business.domain.models.Task
import com.google.gson.annotations.SerializedName

class TaskListSearchResponse(

    @SerializedName("results")
    var results: List<TaskDto>,

    @SerializedName("count")
    var detail: String
)

fun TaskListSearchResponse.toList(): List<Task>{
    val list: MutableList<Task> = mutableListOf()
    for(dto in results){
        list.add(
            dto.toTask()
        )
    }
    return list
}






