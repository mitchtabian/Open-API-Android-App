package com.templateapp.cloudapi.presentation.auth

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.templateapp.cloudapi.presentation.UICommunicationListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseAuthFragment: Fragment(){

    val TAG: String = "AppDebug"

    lateinit var uiCommunicationListener: UICommunicationListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            uiCommunicationListener = context as UICommunicationListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement UICommunicationListener" )
        }

    }

}














