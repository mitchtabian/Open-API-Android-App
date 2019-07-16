package com.codingwithmitch.openapi.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.WorkerThread
import com.codingwithmitch.openapi.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class AuthActivity : AppCompatActivity() {

    private val activityJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + activityJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)



    }

    @WorkerThread
    suspend fun makeLongRunningRequest(){
        val result = slowFetch()

        Log.d("Result", result)
    }

    suspend fun slowFetch(): String {
        Thread.sleep(3000)
        return "cool"
    }
}
