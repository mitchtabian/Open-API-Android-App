package com.templateapp.cloudapi.presentation.util

import android.app.Application
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_ADMIN_ALREADY_EXISTS
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_ALREADY_SENT_REGISTRATION_EMAIL
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_AUTHENTICATE
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_CANT_REGISTER
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_EMAIL_EXISTS
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_FILL_ALL_FIELDS
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_INCORRECT_CURRENT_PASSWORD
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_INCORRECT_PASSWORDS_MISMATCH
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_INVALID_EMAIL
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_INVALID_EMAIL_UPDATES
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_INVALID_UPDATES
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_INVALID_USER_UPDATES
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_NAME_ALREADY_EXISTS
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_NEW_PASSWORD_TOO_SHORT
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_NO_QUERY_PAGINATION_RESULT
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_PASSWORDS_MISMATCH
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_PASSWORD_TOO_SHORT
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_ROLE_NOT_ADMIN
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_SERVER_INTERNAL
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_TASK_DOES_NOT_EXIST_ON_THE_SERVER
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_UNABLE_TO_AUTHENTICATE
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.ERROR_USER_DOES_NOT_EXIST_ON_THE_SERVER
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.RESPONSE_PASSWORD_RESET_SUCCESS
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.RESPONSE_PASSWORD_UPDATE_SUCCESS
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.RESPONSE_TASK_CREATED_SUCCESS
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.RESPONSE_TASK_DELETED_SUCCESS
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.RESPONSE_TASK_OWNER_NO
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.RESPONSE_TASK_OWNER_YES
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.RESPONSE_TASK_UPDATED_SUCCESS
import com.templateapp.cloudapi.presentation.util.ServerMsgStrings.Companion.RESPONSE_USER_UPDATE_SUCCESS

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
        ERROR_NAME_ALREADY_EXISTS to application.getString(R.string.error_name_already_exists),
        ERROR_INVALID_EMAIL to application.getString(R.string.error_invalid_email),
        ERROR_PASSWORD_TOO_SHORT to application.getString(R.string.error_password_too_short),
        ERROR_NEW_PASSWORD_TOO_SHORT to application.getString(R.string.error_new_password_too_short),
        ERROR_UNABLE_TO_AUTHENTICATE to application.getString(R.string.error_unable_to_authenticate),
        ERROR_INVALID_USER_UPDATES to application.getString(R.string.error_invalid_user_updates),
        ERROR_INVALID_EMAIL_UPDATES to application.getString(R.string.error_invalid_email_updates),
        ERROR_INCORRECT_CURRENT_PASSWORD to application.getString(R.string.error_incorrect_current_password),
        ERROR_INCORRECT_PASSWORDS_MISMATCH to application.getString(R.string.error_incorrect_password_mismatch),
        ERROR_NO_QUERY_PAGINATION_RESULT to application.getString(R.string.error_no_query_pagination_result),
        ERROR_TASK_DOES_NOT_EXIST_ON_THE_SERVER to application.getString(R.string.error_task_does_not_exist_on_the_server),
        ERROR_INVALID_UPDATES to application.getString(R.string.error_invalid_updates),
        ERROR_USER_DOES_NOT_EXIST_ON_THE_SERVER to application.getString(R.string.error_user_does_not_exist_on_the_server),
        ERROR_ADMIN_ALREADY_EXISTS to application.getString(R.string.error_admin_already_exists),
        ERROR_ROLE_NOT_ADMIN to application.getString(R.string.error_role_not_admin),
        ERROR_CANT_REGISTER to application.getString(R.string.error_cant_register),
        ERROR_ALREADY_SENT_REGISTRATION_EMAIL to application.getString(R.string.error_already_sent_registration_email),
        ERROR_FILL_ALL_FIELDS to application.getString(R.string.error_fill_all_fields),


        ERROR_SERVER_INTERNAL to application.getString(R.string.error_internal_server_error),

        RESPONSE_USER_UPDATE_SUCCESS to application.getString(R.string.response_user_update_success),
        RESPONSE_TASK_CREATED_SUCCESS to application.getString(R.string.response_task_created_success),
        RESPONSE_TASK_UPDATED_SUCCESS to application.getString(R.string.response_task_updated_success),
        RESPONSE_TASK_DELETED_SUCCESS to application.getString(R.string.response_task_deleted_success),
        RESPONSE_TASK_OWNER_YES to application.getString(R.string.response_task_owner_yes),
        RESPONSE_TASK_OWNER_NO to application.getString(R.string.response_task_owner_no),

        RESPONSE_PASSWORD_RESET_SUCCESS to application.getString(R.string.response_password_reset_success),
        RESPONSE_PASSWORD_UPDATE_SUCCESS to application.getString(R.string.response_password_update_success),


        ERROR_ADMIN_ALREADY_EXISTS to application.getString(R.string.error_admin_already_exists),
        ERROR_ROLE_NOT_ADMIN to application.getString(R.string.error_role_not_admin),
        ERROR_AUTHENTICATE to application.getString(R.string.please_authenticate),





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
        System.err.println(from)
        from?.let{
            translationsStrings.get(from)?.let { return it }; return "[NO TRANSLATION] $from"
        }
        return return "[NO VALUE]"
    }

}