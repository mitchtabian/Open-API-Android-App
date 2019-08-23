package com.codingwithmitch.openapi.ui


interface DataStateChangeListener {

    fun onDataStateChange(dataState: DataState<*>?)

    fun hideSoftKeyboard()

    fun setActionBarTitle(title: String)
}