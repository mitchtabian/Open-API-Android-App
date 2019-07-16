package com.codingwithmitch.di

import com.codingwithmitch.openapi.ui.AuthActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeAuthActivity(): AuthActivity

}













