package com.templateapp.cloudapi.business.domain.util

class ErrorHandling{

    companion object{

        private val TAG: String = "AppDebug"

        const val UNABLE_TO_RESOLVE_HOST = "Unable to resolve host"
        const val UNABLE_TODO_OPERATION_WO_INTERNET = "Can't do that operation without an internet connection"

        const val ERROR_SAVE_ACCOUNT_PROPERTIES = "Error saving account properties.\nTry restarting the app."
        const val ERROR_SAVE_AUTH_TOKEN = "Error saving authentication token.\nTry restarting the app."
        const val ERROR_SOMETHING_WRONG_WITH_IMAGE = "Something went wrong with the image."
        const val ERROR_MUST_SELECT_IMAGE = "You must select an image."

        const val GENERIC_AUTH_ERROR = "Error"
        const val UNAUTHORIZED_ERROR = "Please authenticate"
        const val GENERIC_ERROR = "Error"
        const val INVALID_PAGE = "Invalid page."
        const val ERROR_CHECK_NETWORK_CONNECTION = "Check network connection."
        const val ERROR_UNKNOWN = "Unknown error"
        const val INVALID_CREDENTIALS = "Invalid credentials"
        const val ERROR_PASSWORDS_MUST_MATCH = "Passwords must match."
        const val ERROR_INCORRECT_PASSWORD = "Incorrect password."
        const val ERROR_EMAIL_IN_USE = "account with this email already exists."
        const val ERROR_USERNAME_IN_USE = "account with this username already exists."
        const val ERROR_PASSWORD_BLANK_FIELD = "Password field must not be blank."
        const val ERROR_PASSWORD2_BLANK_FIELD = "Password2 field must not be blank."
        const val ERROR_EMAIL_BLANK_FIELD = "Email field must not be blank."
        const val ERROR_USERNAME_BLANK_FIELD = "Username field must not be blank."
        const val ERROR_UNABLE_TO_RETRIEVE_ACCOUNT_DETAILS = "Unable to retrieve account details. Try logging out."
        const val ERROR_AUTH_TOKEN_INVALID = "Authentication token is invalid. Log out and log back in."
        const val ERROR_PK_INVALID = "Account PK is invalid. Log out and log back in."
        const val ERROR_UPDATE_ACCOUNT = "Unable to update account. Try logging out and logging back in."
        const val ERROR_UPDATE_PASSWORD = "Unable to update password. Try logging out and logging back in."
        const val ERROR_TASK_UNABLE_TO_RETRIEVE = "Unable to retrieve the task. Try reselecting it from the list."
        const val ERROR_NO_PREVIOUS_AUTH_USER = "No previously authenticated user. This error can be ignored."
        const val ERROR_BLANK_FIELD = "This field may not be blank."
        const val SOMETHING_WRONG_WITH_IMAGE = "Something went wrong with the image."
        const val ERROR_SOMETHING_WENT_WRONG = "Something went wrong."
        const val ERROR_DELETE_TASK_DOES_NOT_EXIST = "That task does not exist."
        const val ERROR_DELETE_TASK_NEED_PERMISSION = "You don't have permission to delete that."
        const val ERROR_EDIT_TASK_NEED_PERMISSION = "You don't have permission to edit that."
        const val ERROR_TASK_DOES_NOT_EXIST = "That Task does not exist on the server."

        const val ERROR_SENDING_MAIL = "Unable to send registration mail. Try again later."
        const val ERROR_USER_DOES_NOT_EXIST = "That user does not exist on the server."
        const val ERROR_TASK_TITLE_LENGTH = "Enter a title longer than 5 characters."
        const val ERROR_TASK_BODY_LENGTH = "Enter a body longer than 50 characters."
        const val ERROR_TASK_IMAGE_SIZE = "That image is too large. Images must be less than 2 MB. Try a different image."
        const val ERROR_TASK_IMAGE_ASPECT_RATIO = "Image height must not exceed image width. Try a different image."
        const val ERROR_TASK_MISSING_FIELDS = "You must have a title, some content, and an image."
        const val INVALID_STATE_EVENT = "Invalid state event"
        const val CANNOT_BE_UNDONE = "This can't be undone."
        const val NETWORK_ERROR = "Network error"
        const val NETWORK_ERROR_TIMEOUT = "Network timeout"
        const val CACHE_ERROR_TIMEOUT = "Cache timeout"
        const val UNKNOWN_ERROR = "Unknown error"
        const val ERROR_NOT_CWM_MEMBER = "You must become a member on Codingwithmitch.com to access the API. Visit https://codingwithmitch.com/enroll/"


        fun isNetworkError(msg: String): Boolean{
            when{
                msg.contains(UNABLE_TO_RESOLVE_HOST) -> return true
                else-> return false
            }
        }

        fun isPaginationDone(errorResponse: String?): Boolean{
            // if error response = '{"detail":"Invalid page."}' then pagination is finished
            return errorResponse?.contains(INVALID_PAGE)?: false
        }
    }

}













