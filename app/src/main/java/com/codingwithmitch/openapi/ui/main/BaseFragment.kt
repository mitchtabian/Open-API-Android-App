package com.codingwithmitch.openapi.ui.main

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.codingwithmitch.openapi.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject


abstract class BaseFragment: DaggerFragment(){

    val TAG: String = "AppDebug"

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    /*
        @fragmentId is id of fragment from graph to be EXCLUDED from action back bar nav
     */
    fun setupActionBarWithNavController(fragmentId: Int, activity: AppCompatActivity){
        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            appBarConfiguration
        )
    }


//    fun displayProgressBar(bool: Boolean){
//        if(bool){
//            progressBar.visibility = View.VISIBLE
//        }
//        else{
//            progressBar.visibility = View.GONE
//        }
//    }
//
//    fun displayErrorDialog(errorMessage: String){
//        MaterialDialog(this)
//            .title(R.string.text_error)
//            .message(text = errorMessage){
//                lineSpacing(2F)
//            }
//            .positiveButton(R.string.text_ok)
//            .show()
//    }
}
















