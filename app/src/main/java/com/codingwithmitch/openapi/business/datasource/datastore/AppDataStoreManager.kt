package com.codingwithmitch.openapi.business.datasource.datastore

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private const val APP_DATASTORE = "app"

class AppDataStoreManager(
    val context: Application
): AppDataStore {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(APP_DATASTORE)

    override suspend fun setValue(
        key: Preferences.Key<String>,
        value: String
    ) {
        context.dataStore.edit {
            it[key] = value
        }
    }

    override suspend fun readValue(
        key: Preferences.Key<String>,
    ): String? {
        return context.dataStore.data.first()[key]
    }

}