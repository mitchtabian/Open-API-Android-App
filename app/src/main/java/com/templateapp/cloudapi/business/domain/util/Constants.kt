package com.templateapp.cloudapi.business.domain.util

class Constants {

    companion object{

        const val BASE_URL = "http://192.168.64.121:3000/"
        const val PASSWORD_RESET_URL: String = "http://192.168.64.121:3000/passwordresetform"


        const val NETWORK_TIMEOUT = 6000L
        const val CACHE_TIMEOUT = 2000L
        const val TESTING_NETWORK_DELAY = 0L // fake network delay for testing
        const val TESTING_CACHE_DELAY = 0L // fake cache delay for testing

        const val PAGINATION_PAGE_SIZE = 10 // Size of page we are fetching

        const val GALLERY_REQUEST_CODE = 201
        const val PERMISSIONS_REQUEST_READ_STORAGE: Int = 301
        const val CROP_IMAGE_INTENT_CODE: Int = 401
    }
}