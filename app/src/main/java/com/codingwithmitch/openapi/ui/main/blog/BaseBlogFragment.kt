package com.codingwithmitch.openapi.ui.main.blog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.UICommunicationListener
import com.codingwithmitch.openapi.ui.main.blog.viewmodel.BlogViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
abstract class BaseBlogFragment
constructor(
    @LayoutRes
    private val layoutRes: Int,
    private val viewModelFactory: ViewModelProvider.Factory
): Fragment(layoutRes)
{

    val TAG: String = "AppDebug"

    val viewModel: BlogViewModel by viewModels{
        viewModelFactory
    }

    lateinit var uiCommunicationListener: UICommunicationListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.blogFragment, activity as AppCompatActivity)
        findNavController()
            .addOnDestinationChangedListener(onDestinationChangeListener)
    }

    private val onDestinationChangeListener
            = object: NavController.OnDestinationChangedListener {
        override fun onDestinationChanged(
            controller: NavController,
            destination: NavDestination,
            arguments: Bundle?
        ) {
            setupChannel()
        }
    }

    private fun setupChannel() = viewModel.setupChannel()

    fun setupActionBarWithNavController(fragmentId: Int, activity: AppCompatActivity){
        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            appBarConfiguration
        )
    }

    override fun onDetach() {
        super.onDetach()
        findNavController()
            .removeOnDestinationChangedListener(onDestinationChangeListener)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            uiCommunicationListener = context as UICommunicationListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement UICommunicationListener" )
        }

    }
}