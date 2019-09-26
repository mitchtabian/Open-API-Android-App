package com.codingwithmitch.openapi.ui

import android.util.Log
import com.codingwithmitch.openapi.session.SessionManager
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity: DaggerAppCompatActivity(),
    DataStateChangeListener
{

    val TAG: String = "AppDebug"

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

}











