package com.templateapp.cloudapi.business.datasource.network.responseObjects

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class User(

    @SerializedName("age")
    @Expose
    var age: Int,

    @SerializedName("_id")
    @Expose
    var _id: String,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("email")
    @Expose
    var email: String,

    @SerializedName("createdAt")
    @Expose
    var createdAt: String,

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String,

    @SerializedName("userCreatedSequence")
    @Expose
    var userCreatedSequence: String,

    @SerializedName("__v")
    @Expose
    var __v: Int

)