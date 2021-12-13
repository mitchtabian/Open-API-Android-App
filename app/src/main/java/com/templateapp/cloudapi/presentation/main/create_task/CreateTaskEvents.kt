package com.templateapp.cloudapi.presentation.main.create_task

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.templateapp.cloudapi.business.domain.util.StateMessage

sealed class CreateTaskEvents {

    data class PublishTask(
        val activity: FragmentActivity?
    ): CreateTaskEvents()

    data class OnUpdateTitle(
        val title: String,
    ): CreateTaskEvents()

    data class OnUpdateBody(
        val body: String,
    ): CreateTaskEvents()

    data class OnUpdateUri(
        val uri: Uri,
    ): CreateTaskEvents()


    object OnPublishSuccess: CreateTaskEvents()

    data class Error(val stateMessage: StateMessage): CreateTaskEvents()

    object OnRemoveHeadFromQueue: CreateTaskEvents()
}










