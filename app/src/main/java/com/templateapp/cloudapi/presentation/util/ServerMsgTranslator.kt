package com.templateapp.cloudapi.presentation.util

import android.app.Application
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_EMAIL_EXISTS
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_PASSWORDS_MISMATCH

/*
 * This message translator builds a table of messages returned by server. The translator offers
 * searching by key string.
 */
class ServerMsgTranslator (
    private val application: Application)
{

    /*
    KEY1 = Server message
    KEY2 = UI Message
     */
    private val translationsStrings = hashMapOf(
        /* Server responses */
        ERROR_EMAIL_EXISTS to application.getString(R.string.error_email_exists),
        ERROR_PASSWORDS_MISMATCH to application.getString(R.string.error_passwords_do_not_match),
       /* "Unable to authenticate." to application.getString(R.string.error_unable_to_authenticate),
        "User information update success." to application.getString(R.string.success_user_data_updates),
        "Name already exists." to application.getString(R.string.error_name_exists),
        "Incorrect current password." to application.getString(R.string.error_incorrect_current_password),
        "The passwords do not match." to application.getString(R.string.error_passwords_do_not_match),
        "New password too short." to application.getString(R.string.error_new_password_too_short),
        "Invalid email form provided." to application.getString(R.string.error_invalid_email_provided),
        "Please authenticate" to application.getString(R.string.error_please_authenticate),
        PAGINATION_DONE_ERROR to application.getString(R.string.info_no_more_search_results),
        SUCCESS_TASK_CREATED to  application.getString(R.string.info_task_created_successfully),
        /* Internal API messages */
        ERROR_SAVE_AUTH_TOKEN to application.getString(R.string.error_saving_token),
        SUCCESS_TASK_DELETED to application.getString(R.string.success_task_delete),
        SUCCESS_TASK_UPDATED to application.getString(R.string.success_task_update)*/
    )

    /* Return translations. This function will append [NO TRANSLATION] if no entry found. */
    fun getTranslation(from: String?): String{
        from?.let{
            translationsStrings.get(from)?.let { return it }; return "[NO TRANSLATION] $from"
        }
        return return "[NO VALUE]"
    }

}