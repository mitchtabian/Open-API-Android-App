package com.codingwithmitch.openapi.di

import com.codingwithmitch.openapi.di.auth.AuthFragmentBuildersModule
import com.codingwithmitch.openapi.di.auth.AuthModule
import com.codingwithmitch.openapi.di.auth.AuthScope
import com.codingwithmitch.openapi.di.auth.AuthViewModelModule
import com.codingwithmitch.openapi.di.main.MainFragmentBuildersModule
import com.codingwithmitch.openapi.di.main.MainModule
import com.codingwithmitch.openapi.di.main.MainScope
import com.codingwithmitch.openapi.di.main.MainViewModelModule
import com.codingwithmitch.openapi.ui.auth.AuthActivity
import com.codingwithmitch.openapi.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @MainScope
    @ContributesAndroidInjector(
        modules = [MainModule::class, MainFragmentBuildersModule::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity

}













