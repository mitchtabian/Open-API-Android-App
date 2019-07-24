package com.codingwithmitch.openapi.ui.auth.state

import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.ui.auth.state.AuthState.RegisterState.RegistrationErrors.Companion.mustFillAllFields
import com.codingwithmitch.openapi.ui.auth.state.AuthState.RegisterState.RegistrationErrors.Companion.none
import com.codingwithmitch.openapi.ui.auth.state.AuthState.RegisterState.RegistrationErrors.Companion.passwordsDoNotMatch

data class AuthState(
    var registerState: RegisterState? = RegisterState(),
    var loginState: LoginState? = LoginState(),
    var authToken: AuthToken? = AuthToken()
)
{
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

                return mustFillAllFields()
            }

            if(!password.equals(passwordConfirm)){
                return passwordsDoNotMatch()
            }

            return none()
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
        override fun toString(): String {
            return "LoginState(email=$email, password=$password)"
        }
    }

    override fun toString(): String {
        return "\nAuthState(registerState=${registerState.toString()}" +
                "\nloginState=${loginState.toString()}" +
                "\nauthToken=${authToken.toString()})"
    }


}