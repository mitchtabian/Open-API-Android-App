package com.codingwithmitch.openapi.presentation.main.create_blog

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.codingwithmitch.openapi.presentation.UICommunicationListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseCreateBlogFragment : Fragment()
{

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