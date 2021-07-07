package com.codingwithmitch.openapi.business.datasource.datastore

import androidx.datastore.preferences.core.Preferences

interface AppDataStore {

    suspend fun setValue(
        key: Preferences.Key<String>,
        value: String
    )

    suspend fun readValue(
        key: Preferences.Key<String>,
    ): String?


}