package com.codingwithmitch.openapi.datasource.datastore

import androidx.datastore.preferences.core.Preferences
import com.codingwithmitch.openapi.business.datasource.datastore.AppDataStore

class AppDataStoreManagerFake: AppDataStore {

    private val datastore: MutableMap<Preferences.Key<String>, String> = mutableMapOf()

    override suspend fun setValue(key: Preferences.Key<String>, value: String) {
        datastore[key] = value
    }

    override suspend fun readValue(key: Preferences.Key<String>): String? {
        return datastore[key]
    }
}