package com.codingwithmitch.openapi.presentation

interface UICommunicationListener {

    fun displayProgressBar(isLoading: Boolean)

    fun expandAppBar()

    fun hideSoftKeyboard()

    fun isStoragePermissionGranted(): Boolean
}