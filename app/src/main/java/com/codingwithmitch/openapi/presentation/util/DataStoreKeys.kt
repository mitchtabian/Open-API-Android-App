package com.codingwithmitch.openapi.presentation.util

import androidx.datastore.preferences.core.stringPreferencesKey

class DataStoreKeys {

    companion object{

        // Shared Preference Keys
        val PREVIOUS_AUTH_USER = stringPreferencesKey("com.codingwithmitch.openapi.PREVIOUS_AUTH_USER")
        val BLOG_FILTER = stringPreferencesKey("com.codingwithmitch.openapi.BLOG_FILTER")
        val BLOG_ORDER = stringPreferencesKey( "com.codingwithmitch.openapi.BLOG_ORDER")

    }
}