package com.templateapp.cloudapi.presentation.main.devices

import com.templateapp.cloudapi.business.domain.util.StateMessage
import com.templateapp.cloudapi.presentation.main.task.list.TaskEvents


sealed class ManageDevicesEvents{

    object GetDevice: ManageDevicesEvents()

    data class Error(val stateMessage: StateMessage): ManageDevicesEvents()

    object OnRemoveHeadFromQueue: ManageDevicesEvents()

    object NextPage: ManageDevicesEvents()
}
