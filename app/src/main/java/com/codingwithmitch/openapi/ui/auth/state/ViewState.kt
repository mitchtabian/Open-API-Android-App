package com.codingwithmitch.openapi.ui.auth.state

import com.codingwithmitch.openapi.ui.auth.state.ViewState.ViewStateValue.*

data class ViewState(
    val viewStateValue: ViewStateValue?,
    val message: String?
) {

    enum class ViewStateValue {
        SHOW_PROGRESS,
        HIDE_PROGRESS,
        SHOW_ERROR_DIALOG,
        CLEAR_ALL,
    }


    companion object{

        fun showProgress(): ViewState {
            return ViewState(SHOW_PROGRESS, "Showing progress bar...")
        }

        fun hideProgress(): ViewState {
            return ViewState(HIDE_PROGRESS, "Hiding progress bar...")
        }

        fun showErrorDialog(message: String): ViewState {
            return ViewState(SHOW_ERROR_DIALOG, message)
        }

        fun clearAll(message: String?): ViewState {
            return ViewState(CLEAR_ALL, message)
        }


    }
}














