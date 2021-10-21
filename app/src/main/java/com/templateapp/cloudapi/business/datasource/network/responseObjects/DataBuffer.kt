package com.templateapp.cloudapi.business.datasource.network.responseObjects

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DataBuffer (
    @SerializedName("type")
    @Expose
    var type: String,

    @SerializedName("data")
    @Expose
    var data: ByteArray,

    @SerializedName("email")
    @Expose
    var email: String,

    @SerializedName("createdAt")
    @Expose
    var createdAt: String,

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String,

    @SerializedName("__v")
    @Expose
    var __v: Int

)
