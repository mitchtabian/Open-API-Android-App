package com.codingwithmitch.openapi.datasource.network.auth

import com.codingwithmitch.openapi.business.domain.util.ErrorHandling

object LoginResponses {
    val email = "mitch_test@gmail.com"
    val password = "password"
    val pk = 1
    val username = "mitch_test"
    val token = "de803edc9ebefa3dee77faea8f34fff3e6b217b5"

    val loginSuccess = "{ \"response\": \"Successfully authenticated.\", \"pk\": $pk, \"email\": \"$email\", \"token\": \"$token\" }"
    val loginFail = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.INVALID_CREDENTIALS}\" }"


}