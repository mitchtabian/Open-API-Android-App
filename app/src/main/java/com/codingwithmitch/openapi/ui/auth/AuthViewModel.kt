package com.codingwithmitch.openapi.ui.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.repository.auth.AuthRepository
import com.codingwithmitch.openapi.ui.auth.state.*
import com.codingwithmitch.openapi.ui.auth.state.AuthDataState.*
import com.codingwithmitch.openapi.util.PreferenceKeys
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val sharedPreferences: SharedPreferences,
    val authRepository: AuthRepository
    ): ViewModel()
{
    private val TAG: String = "AppDebug"


    private val viewState = MediatorLiveData<AuthViewState>() // fields
    private val dataState = MediatorLiveData<AuthDataState>() // data

    init{
        setViewState()
    }


    fun observeViewState(): LiveData<AuthViewState> {
        return viewState
    }

    fun observeAuthDataState(): LiveData<AuthDataState> {
        return dataState
    }


    fun checkPreviousAuthUser(){
        Log.d(TAG, "checkPreviousAuthUser: ")

        setDataState(data_state = Loading)

        val previousAuthUserEmail: String? = sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)

        if (previousAuthUserEmail != null) {
            retrieveAuthToken(previousAuthUserEmail)
        }
        else{
            // No previously authenticated user. Wait for user input
            returnNoTokenFound()
        }
    }


    fun retrieveAuthToken(email: String){
        Log.d(TAG, "retrieveAuthToken: found an email saved to preferences: $email. Checking for token in local db...")

        viewModelScope.launch(IO) {

            Log.d(TAG, "retrieveAuthToken: searching by email...")
            authRepository.retrieveAccountPropertiesUsingEmail(email)?.let {

                Log.d(TAG, "retrieveAuthToken: searching for token... account properties: ${it}")
                authRepository.retrieveTokenFromLocalDb(it.pk)?.let { authToken ->

                    Log.d(TAG, "got token.: ")
                    authToken.token?.let {

                        Log.d(TAG, "Found Token: ${it}")

                        setDataState(data_state = Data(AuthToken(authToken.account_pk, authToken.token)))

                    } ?: returnNoTokenFound()

                }?: returnNoTokenFound()

            } ?: returnNoTokenFound()

        }
    }

    fun attemptLogin(){
        viewState.value?.run {
            this.loginFields?.let {
                if(it.isValidForLogin().equals(LoginFields.LoginError.none())){
                    val source = authRepository.attemptLogin(it.login_email!!.toLowerCase(), it.login_password!!)
                    dataState.addSource(source) {
                        setDataState(data_state = it)
                        when(it){
                            is Error -> {
                                dataState.removeSource(source)
                            }
                            is Data ->{
                                dataState.removeSource(source)
                            }
                        }
                    }
                }
                else{
                    setDataState(data_state = Error(it.isValidForLogin()))
                }
            }
        }
    }


    fun attemptRegistration(){
        viewState.value?.run{
            this.registrationFields?.let{

                if(it.isValidForRegistration().equals(RegistrationFields.RegistrationError.none())){
                    val source = authRepository.attemptRegistration(
                        it.registration_email!!,
                        it.registration_username!!,
                        it.registration_password!!,
                        it.registration_confirm_password!!)
                    dataState.addSource(source){
                        setDataState(data_state = it)
                        when(it){
                            is Error -> {
                                dataState.removeSource(source)
                            }
                            is Data ->{
                                dataState.removeSource(source)
                            }
                        }
                    }
                }
                else{
                    setDataState(data_state = Error(it.isValidForRegistration()))
                }
            }
        }
    }


    fun setDataState(
        data_state: AuthDataState? = null
    ){
        viewModelScope.launch(Main) {
            data_state?.let {
                dataState.value = it
            }
        }

    }

    fun setViewState(

        // Registration Fields
        registration_email: String? = null,
        registration_username: String? = null,
        registration_password: String? = null,
        registration_confirm_password: String? = null,

        // Login Fields
        login_email: String? = null,
        login_password: String? = null

    ){
        viewModelScope.launch(Main) {
            viewState.value?.run {
                // Registration Fields
                if(this.registrationFields == null){
                    this.registrationFields = RegistrationFields()
                }
                registration_email?.let{ this.registrationFields?.registration_email = it }
                registration_username?.let {this.registrationFields?.registration_username = it}
                registration_password?.let {this.registrationFields?.registration_password = it}
                registration_confirm_password?.let {this.registrationFields?.registration_confirm_password = it}

                // Login Fields
                if(this.loginFields == null){
                    this.loginFields = LoginFields()
                }
                login_email?.let {this.loginFields?.login_email = it}
                login_password?.let {this.loginFields?.login_password = it}

                // update the LiveData
                viewState.value = this
            }?: initViewState(viewState)
        }
    }

    fun initViewState(viewState: MutableLiveData<AuthViewState>){
        viewState.value = AuthViewState()
    }


    fun returnNoTokenFound(){
        Log.d(TAG, "No token found in local db. Waiting for user input...")
        setViewState()
        setDataState(data_state = Data(null))
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}




























