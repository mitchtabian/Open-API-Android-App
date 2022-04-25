package com.templateapp.cloudapi.business.datasource.cache.account

import androidx.room.*
import com.google.gson.Gson
import com.templateapp.cloudapi.business.datasource.cache.auth.AuthTokenEntity
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.domain.models.Role
import org.json.JSONObject

class RoleEntity {

    @TypeConverter
    fun fromSource(source: Role): String {
        return JSONObject().apply {
            put("_id", source._id)
            put("title", source.title)
        }.toString()
    }

    @TypeConverter
    fun toSource(source: String): Role {
        val json = JSONObject(source)
        return Role(json.getString("_id"), json.getString("title"))
    }
}

