package com.templateapp.cloudapi.presentation.main.account.detail

import com.templateapp.cloudapi.presentation.main.account.settings.SettingsEvents
import com.templateapp.cloudapi.presentation.main.account.update.UpdateAccountEvents


sealed class AccountEvents{

    object ManageUsers: AccountEvents()


    object ManageDevices: AccountEvents()

    data class CheckIfAdmin( val id: String): AccountEvents()

    object GetAccount: AccountEvents()

    object MyAccount: AccountEvents()

    object OnRemoveHeadFromQueue: AccountEvents()

    object OnAdmin: AccountEvents()

}
