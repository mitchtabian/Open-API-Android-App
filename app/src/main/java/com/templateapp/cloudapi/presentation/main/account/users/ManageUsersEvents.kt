package com.templateapp.cloudapi.presentation.main.account.users

import com.templateapp.cloudapi.business.domain.util.StateMessage
import com.templateapp.cloudapi.presentation.main.task.list.TaskEvents


sealed class ManageUsersEvents{

    object GetUsers: ManageUsersEvents()

    data class Error(val stateMessage: StateMessage): ManageUsersEvents()

    object OnRemoveHeadFromQueue: ManageUsersEvents()

    object NextPage: ManageUsersEvents()
}
