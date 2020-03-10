package com.codingwithmitch.openapi

import android.app.Application
import com.codingwithmitch.openapi.di.AppComponent
import com.codingwithmitch.openapi.di.DaggerAppComponent
import com.codingwithmitch.openapi.di.auth.AuthComponent

class BaseApplication : Application(){

    lateinit var appComponent: AppComponent

    private var authComponent: AuthComponent? = null

    override fun onCreate() {
        super.onCreate()
        initAppComponent()
    }

    fun releaseAuthComponent(){
        authComponent = null
    }

    fun authComponent(): AuthComponent {
        if(authComponent == null){
            authComponent = appComponent.authComponent().create()
        }
        return authComponent as AuthComponent
    }

    fun initAppComponent(){
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
    }


}