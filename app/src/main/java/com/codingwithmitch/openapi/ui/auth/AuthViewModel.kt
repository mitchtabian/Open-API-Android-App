package com.codingwithmitch.openapi.ui.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.repository.auth.AuthRepository
import com.codingwithmitch.openapi.ui.auth.state.*
import com.codingwithmitch.openapi.ui.auth.state.AuthScreenState.*
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


    private val viewState = MediatorLiveData<ViewState>()
    private val screenState = MediatorLiveData<AuthScreenState>()

    init{
        setViewState()
//        checkPreviousAuthUser()
    }


    fun observeViewState(): LiveData<ViewState> {
        return viewState
    }

    fun observeAuthScreenState(): LiveData<AuthScreenState> {
        return screenState
    }


    fun checkPreviousAuthUser(){
        Log.d(TAG, "checkPreviousAuthUser: ")

        setScreenState(screen_state = Loading)

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

            authRepository.retrieveAccountPropertiesUsingEmail(email)?.let {

                authRepository.retrieveTokenFromLocalDb(it.pk)?.run {

                    this.token?.let {

                        Log.d(TAG, "Found Token: ${this}")

                        setScreenState(screen_state = Data(AuthToken(this.account_pk, this.token)))

                    } ?: returnNoTokenFound()

                }?: returnNoTokenFound()

            } ?: returnNoTokenFound()

        }
    }

    fun attemptLogin(){
        viewState.value?.run {
            this.loginFields?.let {
                if(it.isValidForLogin().equals(LoginFields.LoginError.none())){
                    val source = authRepository.attemptLogin(it.login_email!!, it.login_password!!)
                    screenState.addSource(source) {
                        setScreenState(screen_state = it)
                        when(it){
                            is Error -> {
                                screenState.removeSource(source)
                            }
                            is Data ->{
                                screenState.removeSource(source)
                            }
                        }
                    }
                }
                else{
                    setScreenState(screen_state = Error(it.isValidForLogin()))
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
                    screenState.addSource(source){
                        setScreenState(screen_state = it)
                        when(it){
                            is Error -> {
                                screenState.removeSource(source)
                            }
                            is Data ->{
                                screenState.removeSource(source)
                            }
                        }
                    }
                }
                else{
                    setScreenState(screen_state = Error(it.isValidForRegistration()))
                }
            }
        }
    }


    fun setScreenState(
        screen_state: AuthScreenState? = null
    ){
        viewModelScope.launch(Main) {
            screen_state?.let {
                screenState.value = it
            }
        }

    }

    fun setViewState(

        // RegistrationState
        registration_email: String? = null,
        registration_username: String? = null,
        registration_password: String? = null,
        registration_confirm_password: String? = null,

        // LoginState
        login_email: String? = null,
        login_password: String? = null

    ){
        viewModelScope.launch(Main) {
            viewState.value?.run {
                // RegistrationState
                if(this.registrationFields == null){
                    this.registrationFields = RegistrationFields()
                }
                registration_email?.let{ this.registrationFields?.registration_email = it }
                registration_username?.let {this.registrationFields?.registration_username = it}
                registration_password?.let {this.registrationFields?.registration_password = it}
                registration_confirm_password?.let {this.registrationFields?.registration_confirm_password = it}

                // LoginState
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

    fun initViewState(viewState: MutableLiveData<ViewState>){
        viewState.value = ViewState()
    }


    fun returnNoTokenFound(){
        Log.d(TAG, "No token found in local db. Waiting for user input...")
        setViewState()
        setScreenState(screen_state = Data(null))
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}




























