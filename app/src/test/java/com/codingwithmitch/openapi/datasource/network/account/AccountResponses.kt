package com.codingwithmitch.openapi.datasource.network.account

import com.codingwithmitch.openapi.business.domain.util.ErrorHandling
import com.codingwithmitch.openapi.business.domain.util.SuccessHandling

object AccountResponses {

    val email = "mitch_test@gmail.com"
    val password = "password"
    val newPassword = "password1234"
    val pk = 1
    val username = "mitch_test"
    val token = "de803edc9ebefa3dee77faea8f34fff3e6b217b5"


    // Success
    val getAccountSuccess = "{ \"pk\": $pk, \"email\": \"$email\", \"username\": \"$username\" }"
    val updateAccountSuccess = "{ \"response\": \"${SuccessHandling.SUCCESS_ACCOUNT_UPDATED}\" }"
    val updatePasswordSuccess = "{ \"response\": \"${SuccessHandling.RESPONSE_PASSWORD_UPDATE_SUCCESS}\" }"

    // Failure
    val updateAccountFail = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_SOMETHING_WENT_WRONG}\" }"
    val updateAccountFail_Random = "{ \"random_response\": \"Error\", \"error_message\": \"SOME RANDOM ERROR WHO KNOWS?!\" }"
    val updateAccountFail_emailInUse = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_EMAIL_IN_USE}\" }"
    val updateAccountFail_usernameInUse = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_USERNAME_IN_USE}\" }"
    val updatePasswordFail_incorrectPassword = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_INCORRECT_PASSWORD}\" }"
    val updatePasswordFail_passwordsMustMatch = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_PASSWORDS_MUST_MATCH}\" }"
    val updatePasswordFail_blankField= "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_BLANK_FIELD}\" }"
    val updatePasswordFail_random = "{ \"random_response\": \"Error\", \"error_message\": \"SOME RANDOM ERROR WHO KNOWS\" }"


}













