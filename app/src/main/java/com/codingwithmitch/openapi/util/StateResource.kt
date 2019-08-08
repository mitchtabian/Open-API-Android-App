package com.codingwithmitch.openapi.util

data class StateError(val errorMessage: String, val useDialog: Boolean)
class Loading
data class SuccessResponse(val message: String?, val useDialog: Boolean)