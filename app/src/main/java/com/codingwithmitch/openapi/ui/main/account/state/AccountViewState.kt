package com.codingwithmitch.openapi.ui.main.account.state

data class AccountViewState(
    var uiMessage: UIMessage? = null
) {

    data class UIMessage(
        var message: String
    )
}



















