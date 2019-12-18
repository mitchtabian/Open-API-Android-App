package com.codingwithmitch.openapi.ui.main

import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.viewmodels.ViewModelProviderFactory

/**
 * Provides app-level dependencies to various BaseFragments:
 * 1) BaseBlogFragment
 * 2) BaseCreateBlogFragment
 * 3) BaseAccountFragment
 *
 * Must do this because of process death issue and restoring state.
 * Why?
 * Can't set values that were saved in instance state to ViewModel because Viewmodel
 * hasn't been created yet when onCreate is called.
 */
interface MainDependencyProvider{

    fun getVMProviderFactory(): ViewModelProviderFactory

    fun getGlideRequestManager(): RequestManager
}