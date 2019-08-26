package com.codingwithmitch.openapi.ui.main.account

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.main.BaseMainFragment

abstract class BaseAccountFragment: BaseMainFragment(){

    lateinit var viewModel: AccountViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.accountFragment, activity as AppCompatActivity)
        viewModel = activity?.run {
            ViewModelProviders.of(this, providerFactory).get(AccountViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        // Cancels jobs when switching between fragments in the same graph
        // ex: from AccountFragment to UpdateAccountFragment
        // NOTE: Must call before "subscribeObservers" b/c that will create new jobs for the next fragment
        cancelPreviousJobs()
    }

    fun cancelPreviousJobs(){
        // When a fragment is destroyed make sure to cancel any on-going requests.
        // Note: If you wanted a particular request to continue even if the fragment was destroyed, you could write a
        //       special condition in the repository or something.
        viewModel.cancelRequests()
    }

}


















