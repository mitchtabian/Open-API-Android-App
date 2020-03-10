package com.codingwithmitch.openapi.di.main

import com.codingwithmitch.openapi.ui.main.MainActivity
import com.codingwithmitch.openapi.ui.main.account.BaseAccountFragment
import com.codingwithmitch.openapi.ui.main.blog.BaseBlogFragment
import com.codingwithmitch.openapi.ui.main.create_blog.BaseCreateBlogFragment
import dagger.Subcomponent


@MainScope
@Subcomponent(
    modules = [
        MainModule::class,
        MainViewModelModule::class
    ])
interface MainComponent {

    @Subcomponent.Factory
    interface Factory{

        fun create(): MainComponent
    }

    fun inject(mainActivity: MainActivity)

    fun inject(baseBlogFragment: BaseBlogFragment)

    fun inject(baseAccountFragment: BaseAccountFragment)

    fun inject(baseCreateBlogFragment: BaseCreateBlogFragment)

}