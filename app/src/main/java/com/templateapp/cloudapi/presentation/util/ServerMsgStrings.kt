package com.templateapp.cloudapi.presentation.util

class ServerMsgStrings {
companion object {
    const val ERROR_EMAIL_EXISTS = "{\"error\":\"Email already exists.\"}"
    const val ERROR_PASSWORDS_MISMATCH = "{\"error\":\"The passwords do not match.\"}"
    const val ERROR_NAME_ALREADY_EXISTS= "{\"error\":\"Name already exists.\"}"
    const val ERROR_INVALID_EMAIL= "{\"error\":\"Invalid email form provided.\"}"
    const val ERROR_PASSWORD_TOO_SHORT= "{\"error\":\"Password too short.\"}"
    const val ERROR_NEW_PASSWORD_TOO_SHORT= "{\"error\":\"New password too short.\"}"
    const val ERROR_UNABLE_TO_AUTHENTICATE= "{\"error\":\"Unable to authenticate.\"}"
    const val ERROR_INVALID_USER_UPDATES= "{\" error\":\"Invalid updates!\"}"
    const val ERROR_INVALID_EMAIL_UPDATES= "{\" error\":\"Invalid updates!\"}"
    const val ERROR_INCORRECT_CURRENT_PASSWORD= "{\"error\":\"Incorrect current password.\"}"
    const val ERROR_INCORRECT_PASSWORDS_MISMATCH= "{\"error\":\"The passwords do not match.\"}"
    const val ERROR_NO_QUERY_PAGINATION_RESULT= "{\"error\":\"No query/pagination result.\"}"
    const val ERROR_TASK_DOES_NOT_EXIST_ON_THE_SERVER= "{\"error\":\"That Task does not exist on the server.\"}"
    const val ERROR_INVALID_UPDATES= "{\"error\":\"Invalid updates!\"}"

    //html errors

   // const val ERROR_NEW_PASSWORD_TOO_SHORT= "{\"error\":\"New password too short. Please choose a new one.\"}"
    const val ERROR_SERVER_INTERNAL= "{\"error\":\"Internal server error. Please try again.\"}"

   //responses

    const val RESPONSE_USER_UPDATE_SUCCESS= "{\"response\":\"User information update success.\"}"
    const val RESPONSE_TASK_CREATED_SUCCESS= "{\"response\":\"Task created successfully.\"}"
    const val RESPONSE_TASK_UPDATED_SUCCESS= "{\"response\":\"Task updated successfully.\"}"
    const val RESPONSE_TASK_DELETED_SUCCESS= "{\"response\":\"Task deleted successfully.\"}"
    const val RESPONSE_TASK_OWNER_YES= "{\"response\":\"You are the owner of the task.\"}"
    const val RESPONSE_TASK_OWNER_NO= "{\"response\":\"You are not the owner of the task.\"}"

    const val RESPONSE_PASSWORD_RESET_SUCCESS= "{\"response\":\"Success. Please close the browser window.\"}"
    const val RESPONSE_PASSWORD_UPDATE_SUCCESS= "{\"response\":\"User password update success.\"}"


    const val ERROR_ADMIN_ALREADY_EXISTS= "{\"error\":\"Unable to make a new account. Admin already exists!'\"}"
    const val ERROR_ROLE_NOT_ADMIN= "{\"error\":\"Unable to make changes. Logged in user is not admin.'\"}"

}
}