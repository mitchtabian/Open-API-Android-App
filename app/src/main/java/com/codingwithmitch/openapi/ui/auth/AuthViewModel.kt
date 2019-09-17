package com.codingwithmitch.openapi.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.codingwithmitch.openapi.api.auth.network_responses.LoginResponse
import com.codingwithmitch.openapi.api.auth.network_responses.RegistrationResponse
import com.codingwithmitch.openapi.repository.auth.AuthRepository
import com.codingwithmitch.openapi.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
): ViewModel(){


    fun testLogin(): LiveData<GenericApiResponse<LoginResponse>>{
        return authRepository.testLoginRequest(
            "mitchelltabian@gmail.com",
            "codingwithmitch1"
        )
    }

    fun testRegister(): LiveData<GenericApiResponse<RegistrationResponse>>{
        return authRepository.testRegistrationRequest(
            "mitchelltabian1234@gmail.com",
            "mitchelltabian1234",
            "codingwithmitch1",
            "codingwithmitch1"
        )
    }

}





























