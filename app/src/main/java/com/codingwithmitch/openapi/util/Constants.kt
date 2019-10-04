package com.codingwithmitch.openapi.util

class Constants {

    companion object{

        const val BASE_URL = "https://open-api.xyz/api/"
        const val PASSWORD_RESET_URL: String = "https://open-api.xyz/password_reset/"

        const val NETWORK_TIMEOUT = 3000L
        const val TESTING_NETWORK_DELAY = 2000L // fake network delay for testing
        const val TESTING_CACHE_DELAY = 0L // fake cache delay for testing
    }
}