package com.codingwithmitch.openapi.ui.auth

import com.codingwithmitch.openapi.ui.auth.AuthResource.AuthStatus.*

data class AuthResource<out T>(
    val authStatus: AuthStatus,
    val data: T?,
    val message: String?
){

//    enum class AuthStatus {
//        AUTHENTICATED, ERROR, LOADING, NOT_AUTHENTICATED
//    }

    enum class AuthStatus {
        AUTHENTICATED, NOT_AUTHENTICATED
    }

    companion object{

//        fun <T> error(msg: String?, data: T?): AuthResource<T> {
//            return AuthResource(ERROR, data, msg)
//        }
//
//        fun <T> loading(data: T?): AuthResource<T> {
//            return AuthResource(LOADING, data, null)
//        }

        fun <T> logout(): AuthResource<T> {
            return AuthResource(NOT_AUTHENTICATED, null, null)
        }

        fun <T> authenticate(data: T): AuthResource<T> {
            return AuthResource(AUTHENTICATED, data, null)
        }
    }

}








