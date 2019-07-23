package com.codingwithmitch.openapi.di.main

import com.codingwithmitch.openapi.ui.main.MainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeMainFragment(): MainFragment

}