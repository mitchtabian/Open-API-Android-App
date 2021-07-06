package com.codingwithmitch.openapi.presentation.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.business.domain.util.*

private val TAG: String = "AppDebug"

fun processQueue(
    context: Context?,
    queue: Queue<StateMessage>,
    stateMessageCallback: StateMessageCallback
) {
    context?.let { ctx ->
        if(!queue.isEmpty()){
            queue.peek()?.let { stateMessage ->
                ctx.onResponseReceived(
                    response = stateMessage.response,
                    stateMessageCallback = stateMessageCallback
                )
            }
        }
    }
}

private fun Context.onResponseReceived(
    response: Response,
    stateMessageCallback: StateMessageCallback
) {
    when(response.uiComponentType){

        is UIComponentType.AreYouSureDialog -> {

            response.message?.let {
                areYouSureDialog(
                    message = it,
                    callback = response.uiComponentType.callback,
                    stateMessageCallback = stateMessageCallback
                )
            }
        }

        is UIComponentType.Toast -> {
            response.message?.let {
                displayToast(
                    message = it,
                    stateMessageCallback = stateMessageCallback
                )
            }
        }

        is UIComponentType.Dialog -> {
            displayDialog(
                response = response,
                stateMessageCallback = stateMessageCallback
            )
        }

        is UIComponentType.None -> {
            // This would be a good place to send to your Error Reporting
            // software of choice (ex: Firebase crash reporting)
            Log.i(TAG, "onResponseReceived: ${response.message}")
            stateMessageCallback.removeMessageFromStack()
        }
    }
}


private fun Context.displayDialog(
    response: Response,
    stateMessageCallback: StateMessageCallback
){
    response.message?.let { message ->

        when (response.messageType) {

            is MessageType.Error -> {
                displayErrorDialog(
                    message = message,
                    stateMessageCallback = stateMessageCallback
                )
            }

            is MessageType.Success -> {
                displaySuccessDialog(
                    message = message,
                    stateMessageCallback = stateMessageCallback
                )
            }

            is MessageType.Info -> {
                displayInfoDialog(
                    message = message,
                    stateMessageCallback = stateMessageCallback
                )
            }

            else -> {
                // do nothing
                stateMessageCallback.removeMessageFromStack()
                null
            }
        }
    }?: stateMessageCallback.removeMessageFromStack()
}

private fun Context.displaySuccessDialog(
    message: String?,
    stateMessageCallback: StateMessageCallback
) {
    MaterialDialog(this)
        .show{
            title(R.string.text_success)
            message(text = message)
            positiveButton(R.string.text_ok){
                stateMessageCallback.removeMessageFromStack()
                dismiss()
            }
            onDismiss {
            }
            cancelable(false)
        }
}

private fun Context.displayErrorDialog(
    message: String?,
    stateMessageCallback: StateMessageCallback
) {
    MaterialDialog(this)
        .show{
            title(R.string.text_error)
            message(text = message)
            positiveButton(R.string.text_ok){
                stateMessageCallback.removeMessageFromStack()
                dismiss()
            }
            onDismiss {
            }
            cancelable(false)
        }
}

private fun Context.displayInfoDialog(
    message: String?,
    stateMessageCallback: StateMessageCallback
) {
    MaterialDialog(this)
        .show{
            title(R.string.text_info)
            message(text = message)
            positiveButton(R.string.text_ok){
                stateMessageCallback.removeMessageFromStack()
                dismiss()
            }
            onDismiss {
            }
            cancelable(false)
        }
}

private fun Context.areYouSureDialog(
    message: String,
    callback: AreYouSureCallback,
    stateMessageCallback: StateMessageCallback
) {
    MaterialDialog(this)
        .show{
            title(R.string.are_you_sure)
            message(text = message)
            negativeButton(R.string.text_cancel){
                callback.cancel()
                stateMessageCallback.removeMessageFromStack()
                dismiss()
            }
            positiveButton(R.string.text_yes){
                callback.proceed()
                stateMessageCallback.removeMessageFromStack()
                dismiss()
            }
            onDismiss {
            }
            cancelable(false)
        }
}

fun Context.displayToast(
    @StringRes message:Int,
    stateMessageCallback: StateMessageCallback
){
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    stateMessageCallback.removeMessageFromStack()
}

fun Context.displayToast(
    message:String,
    stateMessageCallback: StateMessageCallback
){
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    stateMessageCallback.removeMessageFromStack()
}


