package com.codingwithmitch.openapi.business.datasource.datastore

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val APP_DATASTORE = "app"

class DataStoreManager(val context: Application) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(APP_DATASTORE)

    suspend fun <T> setValue(
        key: Preferences.Key<T>,
        value: T
    ) {
        context.dataStore.edit {
            it[key] = value
        }
    }

    fun <T> readValue(
        key: Preferences.Key<T>,
    ): Flow<T?> {
        return context.dataStore.data
            .map { preferences ->
                // No type safety.
                preferences[key]
            }
    }

}