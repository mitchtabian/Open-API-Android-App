package com.codingwithmitch.openapi.utils

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.codingwithmitch.openapi.TestApp

/**
 * Custom runner to disable dependency injection.
 */
class OpenApiTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, TestApp::class.java.name, context)
    }
}