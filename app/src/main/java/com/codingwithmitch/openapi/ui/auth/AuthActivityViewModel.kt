package com.codingwithmitch.openapi.ui.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingwithmitch.openapi.api.auth.OpenApiAuthService
import com.codingwithmitch.openapi.api.auth.network_responses.RegistrationResponse
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.ui.auth.state.AuthState
import com.codingwithmitch.openapi.ui.auth.state.AuthState.*
import com.codingwithmitch.openapi.ui.auth.state.AuthState.RegisterState.*
import com.codingwithmitch.openapi.ui.auth.state.ViewState
import com.codingwithmitch.openapi.util.PreferenceKeys
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class AuthActivityViewModel
@Inject
constructor(
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor,
    val openApiAuthService: OpenApiAuthService): ViewModel()
{
    private val TAG: String = "AppDebug"


    private val viewState = MutableLiveData<ViewState>()
    private val authState = MediatorLiveData<AuthState>()


    init{
        Log.d(TAG, "init: called.")
        checkPreviousAuthUser()
    }


    fun observeAuthState(): LiveData<AuthState> {
        return authState
    }

    fun observeViewState(): LiveData<ViewState> {
        return viewState
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
        authState.value?.run {
            // RegistrationState
            if(this.registerState == null){
                this.registerState = RegisterState()
            }
            registration_email?.let{ this.registerState?.email = it }
            registration_username?.let {this.registerState?.username = it}
            registration_password?.let {this.registerState?.password = it}
            registration_confirm_password?.let {this.registerState?.passwordConfirm = it}

            // LoginState
            if(this.loginState == null){
                this.loginState = LoginState()
            }
            login_email?.let {this.loginState?.email = it}
            login_password?.let {this.loginState?.password = it}

            // AuthToken
            if(this.authToken == null){
                this.authToken = AuthToken()
            }
            auth_token?.let {this.authToken?.token = it}
            auth_account_pk.let {this.authToken?.account_pk = it}

            // update the LiveData
            authState.value = this
        }
    }

    fun setViewState(v: ViewState){
        if(v.viewStateValue != viewState.value?.viewStateValue){
            viewState.value = v
        }
    }

    fun checkPreviousAuthUser(){
        Log.d(TAG, "checkPreviousAuthUser: ")
        val previousAuthUserEmail: String? = sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)

        if (previousAuthUserEmail != null) {
            retrieveAuthToken(previousAuthUserEmail)
        }
        else{
            // No previously authenticated user. Wait for user input
            Log.d(TAG, "No previously authenticated user. Waiting for user input...")
            authState.value = AuthState(null, null, null)
        }
    }


    fun retrieveAuthToken(email: String){

//        val token: AuthToken? = AuthToken(1, "some_token")
        val token: AuthToken? = null
        // TODO("search auth_token table for token associated with email")


        if(token != null){ // *** this needs work. UNFINISHED
            // found token. User is authenticated.
            Log.d(TAG, "Token found... User is authenticated.")
//            authToken.value = token // trigger navigation to MainActivity

            //TODO("Use Coroutines to set token ? I can I can do with RX. But what about Coroutines?")
            authState.value?.also {
                it.authToken?.token = token.token
                authState.value = it // trigger navigation to MainActivity
            }
        }
        else{
            // No token in local db. Wait for user input
            Log.d(TAG, "No token found in local db for user: $email. Waiting for user input...")
            authState.value = AuthState(null, null, null)
        }
    }

    fun attemptRegistration(){
        authState.value?.run{

            this.registerState?.let{

                if(it.isValidForRegistration().equals(RegistrationErrors.none())){
                    showProgress()

                    // Non-null assert (!!) is OK here b/c "isValidForRegistration"
                    openApiAuthService.register(it.email!!, it.username!!, it.password!!, it.passwordConfirm!!)
                        .enqueue(object: Callback<RegistrationResponse>{

                            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                                Log.e(TAG, t.message)
                                hideProgress()
                                showErrorDialog("Something went wrong. Try re-launching the app.")
                            }

                            override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
//                                Log.d(TAG, response.message())
//                                Log.d(TAG, response.body().toString())
//                                Log.d(TAG, call.request().url().encodedPath())
                                hideProgress()

                                if(response.body()?.response.equals("Error")){
                                    response.body()?.errorMessage?.let { message -> showErrorDialog(message) }
                                }
                                else{
                                    response.body()?.let {
                                        saveAuthenticatedUserToPrefs(it.email)
                                        insertAuthTokenIntoDb(AuthToken(it.pk, it.token))
                                        setAuthenticatedUser(AuthToken(it.pk, it.token))
                                    }
                                }
                            }

                        })
                }
                else{
                    showErrorDialog(it.isValidForRegistration())
                }
            }
        }
    }

    fun showProgress(){
        viewState.value = ViewState.showProgress()
    }

    fun hideProgress(){
        viewState.value = ViewState.hideProgress()
    }

    fun showErrorDialog(errorMessage: String){
        viewState.value = ViewState.showErrorDialog(errorMessage)
    }

    fun saveAuthenticatedUserToPrefs(email: String){
        sharedPrefsEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        sharedPrefsEditor.apply()
    }

    fun insertAuthTokenIntoDb(authToken: AuthToken){
        TODO("insert into auth_token table")
    }

    fun setAuthenticatedUser(authToken: AuthToken){
        setAuthState(
            auth_token = authToken.token,
            auth_account_pk = authToken.account_pk
        )
    }

}




























