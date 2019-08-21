package com.codingwithmitch.openapi.ui.auth

import android.util.Log
import androidx.lifecycle.*
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.repository.auth.AuthRepository
import com.codingwithmitch.openapi.ui.BaseViewModel
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.Loading
import com.codingwithmitch.openapi.ui.auth.state.*
import com.codingwithmitch.openapi.ui.auth.state.AuthStateEvent.*
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
): BaseViewModel<AuthStateEvent, AuthViewState>()
{
    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        when(stateEvent){

            is LoginAttemptEvent -> {

                return authRepository.attemptLogin(
                    stateEvent.email,
                    stateEvent.password
                )
            }

            is RegisterAttemptEvent -> {
                Log.d(TAG, "handleStateEvent: attempting registration...")
                return authRepository.attemptRegistration(
                    stateEvent.email,
                    stateEvent.username,
                    stateEvent.password,
                    stateEvent.confirm_password
                )
            }

            is CheckPreviousAuthEvent -> {
                return authRepository.checkPreviousAuthUser()
            }


        }
    }

    fun setRegistrationFields(registrationFields: RegistrationFields){
        _viewState.value?.let {
            it.registrationFields?.let{
                if(it == registrationFields){
                    return
                }
            }
        }
        val update = _viewState.value?.let {
            it
        }?: AuthViewState()
        update.registrationFields = registrationFields
        _viewState.value = update
    }

    fun setLoginFields(loginFields: LoginFields){
        _viewState.value?.let {
            it.loginFields?.let{
                if(it == loginFields){
                    return
                }
            }
        }
        val update = _viewState.value?.let {
            it
        }?: AuthViewState()
        update.loginFields = loginFields
        _viewState.value = update
    }

    fun setAuthToken(authToken: AuthToken){
        _viewState.value?.let {
            it.authToken?.let{
                if(it == authToken){
                    return
                }
            }
        }
        val update = _viewState.value?.let {
            it
        }?: AuthViewState()
        update.authToken = authToken
        _viewState.value = update
    }

}




























