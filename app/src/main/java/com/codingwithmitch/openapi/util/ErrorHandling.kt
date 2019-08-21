package com.codingwithmitch.openapi.util

import android.util.Log
import org.json.JSONObject

class ErrorHandling{

    class NetworkErrors{

        companion object{

            private val TAG: String = "AppDebug"

            val UNABLE_TO_RESOLVE_HOST = "Unable to resolve host"
            val UNABLE_TODO_OPERATION_WO_INTERNET = "Can't do that operation without an internet connection"

            val ERROR_SAVE_ACCOUNT_PROPERTIES = "Error saving account properties.\nTry restarting the app."
            val ERROR_SAVE_AUTH_TOKEN = "Error saving authentication token.\nTry restarting the app."

            val GENERIC_AUTH_ERROR = "Error"
            val PAGINATION_DONE_ERROR = "Invalid page."


            fun isNetworkError(msg: String): Boolean{
                when{
                    msg.contains(UNABLE_TO_RESOLVE_HOST) -> return true
                    else-> return false
                }
            }

            fun parseDetailJsonResponse(rawJson: String?): String{
                if(!rawJson.isNullOrBlank()){
                    return JSONObject(rawJson).get("detail") as String
                }
                return ""
            }

            fun isPaginationDone(errorResponse: String?): Boolean{
                // if error response = '{"detail":"Invalid page."}' then pagination is finished
                Log.d(TAG, "isPaginationDone: ${parseDetailJsonResponse(errorResponse)}")
                return PAGINATION_DONE_ERROR.equals(parseDetailJsonResponse(errorResponse))
            }
        }
    }

}





















