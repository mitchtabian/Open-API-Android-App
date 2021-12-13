package com.templateapp.cloudapi.presentation.main.task.update

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.templateapp.cloudapi.business.domain.util.StateMessage

sealed class UpdateTaskEvents {

    data class Update(val activity: FragmentActivity?): UpdateTaskEvents()

    data class getTask(val id: String): UpdateTaskEvents()

    data class OnUpdateTitle(val title: String): UpdateTaskEvents()

    data class OnUpdateBody(val body: String): UpdateTaskEvents()

    data class OnUpdateUri(val uri: Uri): UpdateTaskEvents()

    object OnUpdateComplete: UpdateTaskEvents()

    data class Error(val stateMessage: StateMessage): UpdateTaskEvents()

    object OnRemoveHeadFromQueue: UpdateTaskEvents()
}




