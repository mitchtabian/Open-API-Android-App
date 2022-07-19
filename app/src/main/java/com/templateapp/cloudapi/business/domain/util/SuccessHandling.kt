package com.templateapp.cloudapi.business.domain.util

class SuccessHandling {

    companion object{

        const val RESPONSE_PASSWORD_UPDATE_SUCCESS = "successfully changed password"
        const val RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE = "Done checking for previously authenticated user."
       const val RESPONSE_NO_PERMISSION_TO_EDIT = "You are not the owner of the task."
        const val RESPONSE_HAS_PERMISSION_TO_EDIT = "You are the owner of the task."
        const val SUCCESS_TASK_CREATED = "created"
        const val SUCCESS_TASK_DELETED = "deleted"
        const val SUCCESS_TASK_UPDATED = "updated"

        const val SUCCESS_TASK_DOES_NOT_EXIST_IN_CACHE = "Task does not exist in the cache."
        const val SUCCESS_TASK_EXISTS_ON_SERVER = "Task exists on the server and in the cache."

        const val SUCCESS_ACCOUNT_UPDATED = "User information update success."

        const val RESPONSE_REGISTRATION_MAIL_SENT= "Registration mail sent."
        const val SUCCESS_PASSWORD_UPDATED = "User password update success."

        const val SUCCESS_LOGOUT = "Logout success."


    }
}