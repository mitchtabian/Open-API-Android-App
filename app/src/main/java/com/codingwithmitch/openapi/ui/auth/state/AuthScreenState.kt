package com.codingwithmitch.openapi.ui.auth.state

import com.codingwithmitch.openapi.models.AuthToken


sealed class AuthScreenState{
    data class Error(val errorMessage: String): AuthScreenState()
    data class Data(val authToken: AuthToken?): AuthScreenState() // return null to hide loading
    object Loading: AuthScreenState()
}