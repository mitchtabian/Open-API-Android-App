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
import com.codingwithmitch.openapi.BaseApplication
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.util.Constants.Companion.CANNOT_BE_UNDONE
import com.codingwithmitch.openapi.util.Constants.Companion.PERMISSIONS_REQUEST_READ_STORAGE
import com.codingwithmitch.openapi.util.DataState
import com.codingwithmitch.openapi.util.MessageType
import com.codingwithmitch.openapi.util.Response
import com.codingwithmitch.openapi.util.UIComponentType
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity: AppCompatActivity(),
    UICommunicationListener
{

    val TAG: String = "AppDebug"

    abstract fun inject()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as BaseApplication).appComponent
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onResponseReceived(response: Response) {

            when(response.uiComponentType){

                is UIComponentType.AreYouSureDialog -> {
                    response.message?.let {
                        areYouSureDialog(
                            message = it,
                            callback = response.uiComponentType.callback
                        )
                    }
                }

                is UIComponentType.Toast -> {
                    response.message?.let { displayToast(it) }
                }

                is UIComponentType.Dialog -> {
                    displayDialog(response)
                }

                is UIComponentType.None -> {
                    // This would be a good place to send to your Error Reporting
                    // software of choice (ex: Firebase crash reporting)
                    Log.i(TAG, "onUIMessageReceived: ${response.message}")
                }
            }
    }

    private fun displayDialog(response: Response){
        response.message?.let { message ->

            when (response.messageType) {

                is MessageType.Error -> {
                    displayErrorDialog(message)
                }

                is MessageType.Success -> {
                    displaySuccessDialog(message)
                }

                is MessageType.Info -> {
                    displayInfoDialog(message)
                }

                else -> {
                    // do nothing
                }
            }
        }
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
}











