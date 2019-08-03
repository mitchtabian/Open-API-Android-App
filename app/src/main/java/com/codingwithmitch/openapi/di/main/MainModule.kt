package com.codingwithmitch.openapi.di.main

import com.codingwithmitch.openapi.api.main.OpenApiMainService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {


    @MainScope
    @Provides
    fun provideOpenApiMainService(retrofitBuilder: Retrofit.Builder): OpenApiMainService {
        return retrofitBuilder
            .build()
            .create(OpenApiMainService::class.java)
    }

}