package com.codingwithmitch.openapi.ui

import android.os.Bundle
import android.util.Log

import com.codingwithmitch.BaseActivity
import com.codingwithmitch.models.AuthToken
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.api.OpenApiService
import retrofit2.Call

import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class AuthActivity : BaseActivity() {

    @Inject
    lateinit var openApiService: OpenApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)



    }


    fun testRetrieveToken(){
        openApiService.login("mitchelltabian@gmail.com", "Password1234!")
            .enqueue(object: Callback<AuthToken>{

                override fun onFailure(call: Call<AuthToken>, t: Throwable) {
                    Log.e("call", t.message)
                }

                override fun onResponse(call: Call<AuthToken>, response: Response<AuthToken>) {
                    Log.d("call", response.message())
                    Log.d("call", response.body().toString())
                    Log.d("call", call.request().url().encodedPath())
                }

            })
    }
}


