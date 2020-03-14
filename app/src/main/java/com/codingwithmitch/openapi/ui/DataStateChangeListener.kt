package com.codingwithmitch.openapi.ui

import com.codingwithmitch.openapi.util.DataState

interface DataStateChangeListener{

    fun onDataStateChange(dataState: DataState<*>?)

    fun expandAppBar()

    fun hideSoftKeyboard()

    fun isStoragePermissionGranted(): Boolean
}