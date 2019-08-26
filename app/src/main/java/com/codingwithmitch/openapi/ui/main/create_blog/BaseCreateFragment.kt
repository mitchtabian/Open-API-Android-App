package com.codingwithmitch.openapi.ui.main.create_blog

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.main.BaseMainFragment


abstract class BaseCreateFragment: BaseMainFragment(){

    lateinit var viewModel: CreateBlogViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setup back navigation for this graph
        setupActionBarWithNavController(R.id.createBlogFragment, activity as AppCompatActivity)

        viewModel = activity?.run {
            ViewModelProviders.of(this, providerFactory).get(CreateBlogViewModel::class.java)
        }?: throw Exception("Invalid Activity")
    }

    fun cancelPreviousJobs(){
//        TODO("set this up when viewmodel is done")
    }
}
















