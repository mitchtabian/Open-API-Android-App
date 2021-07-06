package com.codingwithmitch.openapi.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.presentation.session.SessionManager
import com.codingwithmitch.openapi.business.domain.util.*
import com.codingwithmitch.openapi.business.domain.util.Constants.Companion.PERMISSIONS_REQUEST_READ_STORAGE
import com.codingwithmitch.openapi.presentation.auth.login.LoginEvents
import javax.inject.Inject

abstract class BaseActivity: AppCompatActivity(),
    UICommunicationListener
{

    val TAG: String = "AppDebug"

    private var dialogInView: MaterialDialog? = null

    @Inject
    lateinit var sessionManager: SessionManager

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

    override fun onPause() {
        super.onPause()
        if(dialogInView != null){
            (dialogInView as MaterialDialog).dismiss()
            dialogInView = null
        }
    }

}









