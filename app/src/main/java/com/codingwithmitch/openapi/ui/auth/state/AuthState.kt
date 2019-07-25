package com.codingwithmitch.openapi.ui.auth.state

import androidx.lifecycle.MutableLiveData
import com.codingwithmitch.openapi.models.AuthToken

data class AuthState(
    var registerState: RegisterState? = RegisterState(),
    var loginState: LoginState? = LoginState(),
    var authToken: AuthToken? = AuthToken(),
    var stateError: StateError? = null
)
{
    private val TAG: String = "AppDebug"


    class StateError(var errorMessage: String){

        companion object{
            fun onError(errorMessage: String?): StateError{
                if(errorMessage == null){
                    return StateError("Unknown error")
                }
                else{
                    return StateError(errorMessage)
                }
            }
        }
    }


    class RegisterState(
        var email: String? = null,
        var username: String? = null,
        var password: String? = null,
        var passwordConfirm: String? = null
    )
    {

        class RegistrationErrors {

            companion object{

                fun mustFillAllFields(): String{
                    return "All fields are required."
                }

                fun passwordsDoNotMatch(): String{
                    return "Passwords must match."
                }

                fun none():String{
                    return "None"
                }

            }
        }

        fun isValidForRegistration(): String{

            if(email.isNullOrEmpty()
                || username.isNullOrEmpty()
                || password.isNullOrEmpty()
                || passwordConfirm.isNullOrEmpty()){

                return RegistrationErrors.mustFillAllFields()
            }

            if(!password.equals(passwordConfirm)){
                return RegistrationErrors.passwordsDoNotMatch()
            }

            return RegistrationErrors.none()
        }

        override fun toString(): String {
            return "RegisterState(email=$email, username=$username, password=$password, passwordConfirm=$passwordConfirm)"
        }
    }


    class LoginState(
        var email: String? = null,
        var password: String? = null
    )
    {
        class LoginErrors {

            companion object{

                fun mustFillAllFields(): String{
                    return "You can't login without an email and password."
                }

                fun none():String{
                    return "None"
                }

            }
        }

        fun isValidForLogin(): String{

            if(email.isNullOrEmpty()
                || password.isNullOrEmpty()){

                return LoginErrors.mustFillAllFields()
            }
            return LoginErrors.none()
        }

        override fun toString(): String {
            return "LoginState(email=$email, password=$password)"
        }
    }

    override fun toString(): String {
        return "\nAuthState(registerState=${registerState.toString()}" +
                "\nloginState=${loginState.toString()}" +
                "\nauthToken=${authToken.toString()})"
    }

    companion object{

        private val TAG: String = "AppDebug"

        fun setAuthState(
            // AuthState obj
            auth_state: MutableLiveData<AuthState>,

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
            auth_state.value?.run {
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
                auth_state.value = this
            }?: initAuthState(auth_state)
        }

        fun initAuthState(auth_state: MutableLiveData<AuthState>){
            auth_state.value = AuthState()
        }
    }
}
















