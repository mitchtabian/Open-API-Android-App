package com.codingwithmitch.openapi

import com.codingwithmitch.openapi.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class BaseApplication: DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates
        return DaggerAppComponent.builder().application(this).build()
    }


}