package com.templateapp.cloudapi.datasource.network.auth

import com.templateapp.cloudapi.business.domain.util.ErrorHandling

object LoginResponses {
    val email = "mitch_test@gmail.com"
    val password = "password"
    val id = "1"
    val username = "mitch_test"
    val token = "de803edc9ebefa3dee77faea8f34fff3e6b217b5"

    val loginSuccess = "{ \"response\": \"Successfully authenticated.\", \"id\": $id, \"email\": \"$email\", \"token\": \"$token\" }"
    val loginFail = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.INVALID_CREDENTIALS}\" }"


}