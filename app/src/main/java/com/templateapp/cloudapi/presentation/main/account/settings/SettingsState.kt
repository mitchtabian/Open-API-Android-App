package com.templateapp.cloudapi.presentation.main.account.settings

import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.StateMessage

data class SettingsState(
    val isLoading: Boolean = false,
    val account: Account? = null,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
