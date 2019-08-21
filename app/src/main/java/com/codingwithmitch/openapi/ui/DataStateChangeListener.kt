package com.codingwithmitch.openapi.ui

import com.codingwithmitch.openapi.ui.DataState

interface DataStateChangeListener {

    fun onDataStateChange(dataState: DataState<*>?)

    fun hideSoftKeyboard()
}