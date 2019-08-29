package com.codingwithmitch.openapi.ui.main.create_blog

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.main.BaseMainFragment
import javax.inject.Inject


abstract class BaseCreateFragment: BaseMainFragment(){

    lateinit var viewModel: CreateBlogViewModel

    @Inject
    lateinit var requestManager: RequestManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setup back navigation for this graph
        setupActionBarWithNavController(R.id.createBlogFragment, activity as AppCompatActivity)

        viewModel = activity?.run {
            ViewModelProviders.of(this, providerFactory).get(CreateBlogViewModel::class.java)
        }?: throw Exception("Invalid Activity")
    }

    fun cancelPreviousJobs(){
        // When a fragment is destroyed make sure to cancel any on-going requests.
        // Note: If you wanted a particular request to continue even if the fragment was destroyed, you could write a
        //       special condition in the repository or something.
        viewModel.cancelRequests()
    }
}
















