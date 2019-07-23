package com.codingwithmitch.openapi.ui.auth


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.api.auth.OpenApiAuthService
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class LoginFragment : DaggerFragment() {

    lateinit var viewModel: AuthActivityViewModel

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this, providerFactory).get(AuthActivityViewModel::class.java)
        }?: throw Exception("Invalid Activity")
    }

    //    fun testLogin(){
//        openApiAuthService.login("mitchelltabian@gmail.com", "Password1234!")
//            .enqueue(object: Callback<AuthToken> {
//
//                override fun onFailure(call: Call<AuthToken>, t: Throwable) {
//                    Log.e("call", t.message)
//                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
//
//                }
//
//                override fun onResponse(call: Call<AuthToken>, response: Response<AuthToken>) {
//                    Log.d("call", response.message())
//                    Log.d("call", response.body().toString())
//                    Log.d("call", call.request().url().encodedPath())
//
//                    val token: String? = response.body()?.token
//                    val pk: Int? = response.body()?.account_pk
//                    if(token != null && pk != null){
//                        TODO("Navigate to MainActivity. User is authenticated")
//                    }
//                    else{
//                        if(token == null){
//                            Log.e("call", "Couldn't retrieve token")
//                        }
//                        if(pk == null){
//                            Log.e("call", "Couldn't retrieve user id")
//                        }
//                        Toast.makeText(activity, "Unable to authenticate", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//            })
//    }
}
