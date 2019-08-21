package com.codingwithmitch.openapi.ui

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.session.SessionManager
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(),
    DataStateChangeListener
{
    private val TAG: String = "AppDebug"

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let{
            displayProgressBar(dataState.loading.isLoading)

            dataState.error?.let {
                handleStateError(it)
            }

            dataState.data?.let {
                it.response?.let {
                    handleStateResponse(it)
                }
            }
        }
    }

    abstract fun displayProgressBar(bool: Boolean)

    fun displayErrorDialog(errorMessage: String?){
        MaterialDialog(this)
            .show{
                title(R.string.text_error)
                message(text = errorMessage)
                positiveButton(R.string.text_ok)
            }
    }

    fun displaySuccessDialog(message: String?){
        MaterialDialog(this)
            .show{
                title(R.string.text_success)
                message(text = message)
                positiveButton(R.string.text_ok)
            }
    }

    private fun handleStateResponse(event: Event<Response>){
        event.getContentIfNotHandled()?.let{
            if(it.useDialog){
                it.message?.let{message ->
                    displaySuccessDialog(message)
                }
            }
            else if(it.useToast){
                it.message?.let{message ->
                    displayToast(message)
                }
            }
            else{
                Log.i(TAG, "handleStateResponse: ${it.message}")
            }
        }
    }

    private fun handleStateError(event: Event<StateError>){
        event.getContentIfNotHandled()?.let{
            if(it.response.useDialog){
                displayErrorDialog(it.response.message)
            }
            else{
                displayToast(it.response.message)
            }
        }
    }

    private fun displayToast(message: String?){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



    override fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }
}




























