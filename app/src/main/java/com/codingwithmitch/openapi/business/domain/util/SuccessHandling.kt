package com.codingwithmitch.openapi.business.domain.util

class SuccessHandling {

    companion object{

        const val RESPONSE_PASSWORD_UPDATE_SUCCESS = "successfully changed password"
        const val RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE = "Done checking for previously authenticated user."
        const val RESPONSE_MUST_BECOME_CODINGWITHMITCH_MEMBER = "You must become a member on Codingwithmitch.com to access the API. Visit https://codingwithmitch.com/enroll/"
        const val RESPONSE_NO_PERMISSION_TO_EDIT = "You don't have permission to edit that."
        const val RESPONSE_HAS_PERMISSION_TO_EDIT = "You have permission to edit that."
        const val SUCCESS_BLOG_CREATED = "created"
        const val SUCCESS_BLOG_DELETED = "deleted"
        const val SUCCESS_BLOG_UPDATED = "updated"

        const val SUCCESS_BLOG_DOES_NOT_EXIST_IN_CACHE = "Blog does not exist in the cache."
        const val SUCCESS_BLOG_EXISTS_ON_SERVER = "Blog exists on the server and in the cache."

        const val SUCCESS_ACCOUNT_UPDATED = "Account update success"
        const val SUCCESS_PASSWORD_UPDATED = "successfully changed password"

        const val SUCCESS_LOGOUT = "Logout success."


    }
}