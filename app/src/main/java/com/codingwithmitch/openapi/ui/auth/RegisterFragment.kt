package com.codingwithmitch.openapi.ui.auth


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.api.auth.OpenApiAuthService
import com.codingwithmitch.openapi.api.auth.network_responses.RegistrationResponse
import com.codingwithmitch.openapi.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class RegisterFragment : DaggerFragment() {

    lateinit var viewModel: AuthActivityViewModel

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this, providerFactory).get(AuthActivityViewModel::class.java)
        }?: throw Exception("Invalid Activity")
    }

//    fun testRegister(){
//        openApiAuthService.register(
//            "testingemail1234@tabian.ca",
//            "testinguser",
//            "Password1234!",
//            "Password1234!")
//            .enqueue(object: Callback<RegistrationResponse> {
//
//                override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
//                    Log.d("call", response.message())
//                    Log.d("call", response.body().toString())
//                    Log.d("call", call.request().url().encodedPath())
//                }
//
//                override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
//                    Log.e("call", t.message)
//                }
//
//            })
//    }

}
