package com.codingwithmitch.openapi.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.codingwithmitch.openapi.BaseApplication
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.util.*
import com.codingwithmitch.openapi.util.Constants.Companion.PERMISSIONS_REQUEST_READ_STORAGE
import javax.inject.Inject

abstract class BaseActivity: AppCompatActivity(),
    UICommunicationListener
{

    val TAG: String = "AppDebug"

    private val dialogs: HashMap<String, MaterialDialog> = HashMap()

    @Inject
    lateinit var sessionManager: SessionManager

    abstract fun inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as BaseApplication).appComponent
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onResponseReceived(
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

    private fun displayDialog(
        response: Response,
        stateMessageCallback: StateMessageCallback
    ){
        Log.d(TAG, "displayDialog: ")
        response.message?.let { message ->

            if(!dialogs.containsKey(message)){
                when (response.messageType) {

                    is MessageType.Error -> {
                        dialogs.put(
                            response.message,
                            displayErrorDialog(
                                message = message,
                                stateMessageCallback = stateMessageCallback
                            )
                        )
                    }

                    is MessageType.Success -> {
                        dialogs.put(
                            response.message,
                            displaySuccessDialog(
                                message = message,
                                stateMessageCallback = stateMessageCallback
                            )
                        )
                    }

                    is MessageType.Info -> {
                        dialogs.put(
                            response.message,
                            displayInfoDialog(
                                message = message,
                                stateMessageCallback = stateMessageCallback
                            )
                        )
                    }

                    else -> {
                        // do nothing
                        stateMessageCallback.removeMessageFromStack()
                    }
                }
            }
        }?: stateMessageCallback.removeMessageFromStack()
    }

    abstract override fun displayProgressBar(isLoading: Boolean)

    override fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager
                .hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    override fun isStoragePermissionGranted(): Boolean{
        if (
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED  ) {


            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSIONS_REQUEST_READ_STORAGE
            )

            return false
        } else {
            // Permission has already been granted
            return true
        }
    }

    private fun dismissDialogs(){
        for(dialog in dialogs){
            dialog.value.dismiss()
            dialogs.remove(dialog.key)
        }
    }

    override fun onPause() {
        super.onPause()
        dismissDialogs()
    }
}











