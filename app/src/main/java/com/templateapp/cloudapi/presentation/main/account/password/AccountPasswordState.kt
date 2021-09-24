package com.templateapp.cloudapi.presentation.main.account.password

import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.StateMessage

data class AccountPasswordState(
    val isLoading: Boolean = false,
    val isPasswordChangeComplete: Boolean = false,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
