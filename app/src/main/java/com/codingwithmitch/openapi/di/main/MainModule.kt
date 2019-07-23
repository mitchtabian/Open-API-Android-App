package com.codingwithmitch.openapi.di.main

import com.codingwithmitch.openapi.SessionManager
import dagger.Module
import dagger.Provides

@Module
class MainModule {

    @MainScope
    @Provides
    fun provideSessionManager(): SessionManager {
        return SessionManager()
    }

}