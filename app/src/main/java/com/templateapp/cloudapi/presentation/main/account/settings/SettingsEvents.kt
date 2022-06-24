package com.templateapp.cloudapi.presentation.main.account.settings


sealed class SettingsEvents{

    object GetAccount: SettingsEvents()

    object Logout: SettingsEvents()

    object OnRemoveHeadFromQueue: SettingsEvents()

}
