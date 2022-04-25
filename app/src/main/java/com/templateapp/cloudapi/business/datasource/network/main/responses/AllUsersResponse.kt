package com.templateapp.cloudapi.business.datasource.network.main.responses

import com.google.gson.annotations.Expose
import com.templateapp.cloudapi.business.domain.util.DateUtils
import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.network.main.AccountDto
import com.templateapp.cloudapi.business.datasource.network.main.TaskDto
import com.templateapp.cloudapi.business.datasource.network.main.toAccount
import com.templateapp.cloudapi.business.datasource.network.main.toTask
import com.templateapp.cloudapi.business.datasource.network.responseObjects.Task
import com.templateapp.cloudapi.business.domain.models.Account

class AllUsersResponse(

    @SerializedName("count")
    var count: String
)














