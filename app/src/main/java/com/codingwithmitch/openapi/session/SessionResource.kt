package com.codingwithmitch.openapi.session

import com.codingwithmitch.openapi.models.AuthToken

data class SessionResource(
    var authToken: AuthToken? = null,
    var errorMessage: String? = null,
    var loading: Boolean? = null
)