package com.codingwithmitch.openapi.ui

import com.codingwithmitch.openapi.ui.BaseActivity.*

data class UIMessage(
    val message: String,
    val uiMessageType: UIMessageType
    )

sealed class UIMessageType{

    class Toast: UIMessageType()

    class Dialog: UIMessageType()

    class AreYouSureDialog(
        val callback: AreYouSureCallback
    ): UIMessageType()

    class None: UIMessageType()
}