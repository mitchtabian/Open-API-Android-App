package com.codingwithmitch.openapi.datasource.network

object RegisterResponses {
    val email = "mitch_test@gmail.com"
    val password = "password"
    val pk = 1
    val username = "mitch_test"
    val token = "de803edc9ebefa3dee77faea8f34fff3e6b217b5"

    val registerSuccess = "{ \"response\": \"successfully registered new user.\", \"email\": \"mitch_test@gmail.com\", \"username\": \"mitch_test\", \"pk\": 1, \"token\": \"de803edc9ebefa3dee77faea8f34fff3e6b217b5\" }"
    val registerFail_emailInUse = "{ \"error_message\": \"That email is already in use.\", \"response\": \"Error\" }"
    val registerFail_usernameInUse = "{ \"error_message\": \"That username is already in use.\", \"response\": \"Error\" }"
    val registerFail_passwordsMustMatch = "{ \"password\": \"Passwords must match.\" }"
    val registerFail_usernameMissing = "{ \"username\": [ \"This field may not be blank.\" ] }"
    val registerFail_emailMissing = "{ \"email\": [ \"This field may not be blank.\" ] }"
    val registerFail_passwordMissing = "{ \"password\": [ \"This field may not be blank.\" ] }"
    val registerFail_password2Missing = "{ \"password2\": [ \"This field may not be blank.\" ] }"
}