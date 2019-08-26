package com.codingwithmitch.openapi.util

class ErrorHandling{

    class NetworkErrors{

        companion object{

            val UNABLE_TO_RESOLVE_HOST = "Unable to resolve host"
            val UNABLE_TODO_OPERATION_WO_INTERNET = "Can't do that operation without an internet connection"

            val ERROR_SAVE_ACCOUNT_PROPERTIES = "Error saving account properties.\nTry restarting the app."
            val ERROR_SAVE_AUTH_TOKEN = "Error saving authentication token.\nTry restarting the app."

            val GENERIC_AUTH_ERROR = "Error"


            fun isNetworkError(msg: String): Boolean{
                when{
                    msg.contains(UNABLE_TO_RESOLVE_HOST) -> return true
                    else-> return false
                }
            }
        }
    }

}





















