package com.codingwithmitch.openapi.ui.main.blog

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.main.BaseMainFragment

abstract class BaseBlogFragment : BaseMainFragment(){

    lateinit var viewModel: BlogViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setup back navigation for this graph
        setupActionBarWithNavController(R.id.blogFragment, activity as AppCompatActivity)

        viewModel = activity?.run {
            ViewModelProviders.of(this, providerFactory).get(BlogViewModel::class.java)
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

























