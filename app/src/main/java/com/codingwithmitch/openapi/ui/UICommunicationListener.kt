package com.codingwithmitch.openapi.ui

import com.codingwithmitch.openapi.util.Response


interface UICommunicationListener {

    fun onResponseReceived(response: Response)

    fun displayProgressBar(isLoading: Boolean)
}