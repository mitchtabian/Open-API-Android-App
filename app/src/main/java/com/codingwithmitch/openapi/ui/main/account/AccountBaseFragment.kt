package com.codingwithmitch.openapi.ui.main.account

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.main.account.state.AccountDataState
import com.codingwithmitch.openapi.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class AccountBaseFragment: DaggerFragment(){

    val TAG: String = "AppDebug"

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AccountViewModel

    lateinit var accountStateChangeListener: AccountStateChangeListener


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.accountFragment, activity as AppCompatActivity)
        viewModel = activity?.run {
            ViewModelProviders.of(this, providerFactory).get(AccountViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        subscribeObservers()
    }

    private fun subscribeObservers(){
        viewModel.observeDataState().observe(viewLifecycleOwner, Observer {
            // send state to activity for UI updates
            // ex: progress bar and material dialog
            // NOTE: error, loading, successResponse and accountProperties are passed to "accountStateChangeListener"
            //       and action will be taken in MainActivity
            accountStateChangeListener.onAccountDataStateChange(it)

            // send state to child fragments (AccountFragment, UpdateAccountFragment & ChangePasswordFragment)
            // Simple and streamlined way to view state in the different cases
            observeDataFromChildFragment(it)
        })
    }

    abstract fun observeDataFromChildFragment(accountDataState: AccountDataState)

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            accountStateChangeListener = context as AccountStateChangeListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement OnArticleSelectedListene" )
        }
    }

}



