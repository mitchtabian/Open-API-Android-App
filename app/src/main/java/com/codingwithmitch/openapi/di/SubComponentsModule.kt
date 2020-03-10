package com.codingwithmitch.openapi.di

import com.codingwithmitch.openapi.di.auth.AuthComponent
import com.codingwithmitch.openapi.di.main.MainComponent
import dagger.Module

@Module(
    subcomponents = [
        AuthComponent::class,
        MainComponent::class
    ]
)
class SubComponentsModule