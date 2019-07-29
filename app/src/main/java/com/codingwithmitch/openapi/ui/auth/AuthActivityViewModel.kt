package com.codingwithmitch.openapi.ui.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.codingwithmitch.openapi.SessionManager
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.repository.auth.AuthRepository
import com.codingwithmitch.openapi.ui.auth.state.AuthState
import com.codingwithmitch.openapi.ui.auth.state.AuthState.*
import com.codingwithmitch.openapi.ui.auth.state.AuthState.RegisterState.*
import com.codingwithmitch.openapi.ui.auth.state.ViewState
import com.codingwithmitch.openapi.util.PreferenceKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthActivityViewModel
@Inject
constructor(
    val sharedPreferences: SharedPreferences,
    val authRepository: AuthRepository,
    val sessionManager: SessionManager
    ): ViewModel()
{
    private val TAG: String = "AppDebug"


    private val viewState = MutableLiveData<ViewState>()
    private val authState = MediatorLiveData<AuthState>()

    init{
        setAuthState()
        checkPreviousAuthUser()
    }


    fun observeAuthState(): LiveData<AuthState> {
        return authState
    }

    fun observeViewState(): LiveData<ViewState> {
        return viewState
    }


    fun setViewState(v: ViewState){
        if(v.viewStateValue != viewState.value?.viewStateValue){
            viewModelScope.launch(Main) {
                viewState.value = v
            }
        }
    }

    fun checkPreviousAuthUser(){
        Log.d(TAG, "checkPreviousAuthUser: ")

        showProgress()

        val previousAuthUserEmail: String? = sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)

        if (previousAuthUserEmail != null) {
            retrieveAuthToken(previousAuthUserEmail)
        }
        else{
            // No previously authenticated user. Wait for user input
            returnNoTokenFound()
            hideProgress()
        }
    }


    fun retrieveAuthToken(email: String){
        Log.d(TAG, "retrieveAuthToken: found an email saved to preferences: $email. Checking for token in local db...")

        viewModelScope.launch(IO) {

            authRepository.retrieveAccountPropertiesUsingEmail(email)?.let {

                authRepository.retrieveTokenFromLocalDb(it.pk)?.run {

                    this.token?.let {

                        Log.d(TAG, "Found Token: ${this}")

                        setAuthState(
                            auth_account_pk = this.account_pk,
                            auth_token = this.token
                        )

                    } ?: returnNoTokenFound()

                }?: returnNoTokenFound()

            } ?: returnNoTokenFound()

            hideProgress()
        }
    }

    fun attemptLogin(){
        authState.value?.run{
            this.loginState?.let {
                if(it.isValidForLogin().equals(LoginState.LoginErrors.none())){

                    showProgress()

                    // Non-null assert (!!) is OK here b/c "isValidForRegistration"
                    val source: LiveData<AuthState> = authRepository.attemptLogin(it.email!!,  it.password!!)
                    authState.addSource(source){

                        it.authToken?.let {

                            sessionManager.setValue(it)
                            setAuthState(
                                auth_token = it.token,
                                auth_account_pk = it.account_pk
                            )
                        }

                        it.stateError?.let{
                            showErrorDialog(it.errorMessage)
                        }

                        hideProgress()

                        authState.removeSource(source)
                    }

                }
                else{
                    showErrorDialog(it.isValidForLogin())
                }
            }
        }
    }


    fun attemptRegistration(){

        authState.value?.run{
            this.registerState?.let{

                if(it.isValidForRegistration().equals(RegistrationErrors.none())){

                    showProgress()
                    // Non-null assert (!!) is OK here b/c "isValidForRegistration"
                    val source: LiveData<AuthState> = authRepository.attemptRegistration(it.email!!, it.username!!, it.password!!, it.passwordConfirm!!)
                    authState.addSource(source){

                        it.authToken?.let {

                            sessionManager.setValue(it)
                            setAuthState(
                                auth_token = it.token,
                                auth_account_pk = it.account_pk
                            )
                        }

                        it.stateError?.let{
                            showErrorDialog(it.errorMessage)
                        }

                        hideProgress()

                        authState.removeSource(source)
                    }

                }
                else{
                    showErrorDialog(it.isValidForRegistration())
                }
            }
        }
    }

    fun setAuthState(
        // RegistrationState
        registration_email: String? = null,
        registration_username: String? = null,
        registration_password: String? = null,
        registration_confirm_password: String? = null,

        // LoginState
        login_email: String? = null,
        login_password: String? = null,

        // AuthToken
        auth_token: String? = null,
        auth_account_pk: Int? = -1
    ){
        viewModelScope.launch(Main) {
            AuthState.setAuthState(
                authState,
                registration_email,
                registration_username,
                registration_password,
                registration_confirm_password,
                login_email,
                login_password,
                auth_token,
                auth_account_pk
            )
        }

    }

    fun returnNoTokenFound(){
        Log.d(TAG, "No token found in local db. Waiting for user input...")
        setAuthState()
    }

    fun showProgress(){
        viewModelScope.launch(Main) {
            viewState.value = ViewState.showProgress()
        }
    }

    fun hideProgress(){
        viewModelScope.launch(Main) {
            viewState.value = ViewState.hideProgress()
        }
    }

    fun showErrorDialog(errorMessage: String){
        viewModelScope.launch(Main) {
            viewState.value = ViewState.showErrorDialog(errorMessage)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}




























