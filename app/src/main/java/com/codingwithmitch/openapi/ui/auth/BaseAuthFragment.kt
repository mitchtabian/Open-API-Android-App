package com.codingwithmitch.openapi.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.codingwithmitch.openapi.di.Injectable
import com.codingwithmitch.openapi.ui.main.MainDependencyProvider
import com.codingwithmitch.openapi.ui.auth.state.AUTH_VIEW_STATE_BUNDLE_KEY
import com.codingwithmitch.openapi.ui.auth.state.AuthViewState
import javax.inject.Inject

abstract class BaseAuthFragment: Fragment(), Injectable{

    val TAG: String = "AppDebug"

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    lateinit var viewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run {
            ViewModelProvider(
                this,
                providerFactory
            ).get(AuthViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        cancelActiveJobs()
    }


    fun isViewModelInitialized() = ::viewModel.isInitialized

    /**
     * !IMPORTANT!
     * Must save ViewState b/c in event of process death the LiveData in ViewModel will be lost
     */
    override fun onSaveInstanceState(outState: Bundle) {
        if(isViewModelInitialized()){
            outState.putParcelable(
                AUTH_VIEW_STATE_BUNDLE_KEY,
                viewModel.viewState.value
            )
        }
        super.onSaveInstanceState(outState)
    }

    /**
     * Restore ViewState after process death
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let { inState ->
            (inState[AUTH_VIEW_STATE_BUNDLE_KEY] as AuthViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    private fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }
}



























