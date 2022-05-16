package com.templateapp.cloudapi.business.datasource.cache.account

import androidx.room.*
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.Role
import dagger.Provides
import org.json.JSONObject


@Entity(
    tableName = "role_properties",
)
data class RoleEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "_id")
    val _id: String,

    @ColumnInfo(name = "title")
    val title: String,


    )


fun RoleEntity.toRole(): Role {
    return Role(
        _id = _id,
        title = title,
    )
}

fun Role.toEntity(): RoleEntity {
    return RoleEntity(
        _id = _id,
        title = title,
    )
}