package com.codingwithmitch.openapi.ui

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.session.SessionManager
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(),
    DataStateChangeListener,
    UICommunicationListener
{
    private val TAG: String = "AppDebug"

    @Inject
    lateinit var sessionManager: SessionManager

    interface AreYouSureCallback{

        fun proceed()

        fun cancel()
    }

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

    override fun onUIMessageReceived(uiMessage: UIMessage) {
        when(uiMessage.uiMessageType){

            is UIMessageType.AreYouSureDialog -> {
                areYouSureDialog(uiMessage.message, uiMessage.uiMessageType.callback)
            }

            is UIMessageType.Toast -> {
                displayToast(uiMessage.message)
            }

            is UIMessageType.Dialog -> {
                displayInfoDialog(uiMessage.message)
            }

            is UIMessageType.None -> {
                Log.i(TAG, "onUIMessageReceived: ${uiMessage.message}")
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

    fun displayInfoDialog(message: String?){
        MaterialDialog(this)
            .show{
                title(R.string.text_info)
                message(text = message)
                positiveButton(R.string.text_ok)
            }
    }

    fun areYouSureDialog(message: String, callback: AreYouSureCallback){
        MaterialDialog(this)
            .show{
                title(R.string.are_you_sure)
                message(text = message)
                negativeButton(R.string.text_cancel){
                    callback.cancel()
                }
                positiveButton(R.string.text_yes){
                    callback.proceed()
                }
            }
    }

    private fun handleStateResponse(event: Event<Response>){
        event.getContentIfNotHandled()?.let{

            when(it.responseType){
                is ResponseType.Toast ->{
                    it.message?.let{message ->
                        displayToast(message)
                    }
                }

                is ResponseType.Dialog ->{
                    it.message?.let{ message ->
                        displaySuccessDialog(message)
                    }
                }

                is ResponseType.None -> {
                    Log.i(TAG, "handleStateResponse: ${it.message}")
                }
            }

        }
    }

    private fun handleStateError(event: Event<StateError>){
        event.getContentIfNotHandled()?.let{
            when(it.response.responseType){
                is ResponseType.Toast ->{
                    it.response.message?.let{message ->
                        displayToast(message)
                    }
                }

                is ResponseType.Dialog ->{
                    it.response.message?.let{ message ->
                        displayErrorDialog(message)
                    }
                }

                is ResponseType.None -> {
                    Log.i(TAG, "handleStateError: ${it.response.message}")
                }
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




























