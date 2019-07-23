package com.codingwithmitch.openapi.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class AuthActivity : DaggerAppCompatActivity() {

    private val TAG: String = "AppDebug"

    lateinit var progressBar: ProgressBar

    lateinit var viewModel: AuthActivityViewModel

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        progressBar = findViewById(R.id.progress_bar)

        Log.d(TAG, "AuthActivity: started")

        viewModel = ViewModelProviders.of(this, providerFactory).get(AuthActivityViewModel::class.java)

        subscribeObservers()
    }

    fun subscribeObservers(){
        viewModel.observeViewState().observe(this, Observer {

            when(it){
                AuthActivityViewModel.ViewState.HIDE_PROGRESS ->{
                    displayProgressBar(false)
                }

                AuthActivityViewModel.ViewState.SHOW_PROGRESS ->{
                    displayProgressBar(true)
                }
                else ->{
                    displayProgressBar(false)
                }
            }
        })
    }

    fun displayProgressBar(bool: Boolean){
        if(bool){
            progressBar.visibility = View.VISIBLE
        }
        else{
            progressBar.visibility = View.GONE
        }
    }
}





















