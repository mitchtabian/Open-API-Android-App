package com.codingwithmitch.openapi.ui.auth.state

import com.codingwithmitch.openapi.models.AuthToken


sealed class AuthDataState{
    data class Error(val errorMessage: String): AuthDataState()
    data class Data(val authToken: AuthToken?): AuthDataState() // return null to hide loading
    object Loading: AuthDataState()
}