package com.codingwithmitch.openapi.util

class ErrorHandling{

    class NetworkErrors{

        companion object{

            val UNABLE_TO_RESOLVE_HOST = "Unable to resolve host"

            fun isNetworkError(msg: String): Boolean{
                when{
                    msg.contains(UNABLE_TO_RESOLVE_HOST) -> return true
                    else-> return false
                }
            }
        }
    }

}





















