package com.templateapp.cloudapi.business.datasource.network.main.responses

import com.templateapp.cloudapi.business.domain.models.BlogPost
import com.templateapp.cloudapi.business.domain.util.DateUtils
import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.network.responseObjects.Task
import com.templateapp.cloudapi.business.datasource.network.responseObjects.User

class AccountUpdateResponse(

    @SerializedName("response")
    val response: String?,

    @SerializedName("error")
    val error: String?,

    @SerializedName("user")
    val user: User?
)













