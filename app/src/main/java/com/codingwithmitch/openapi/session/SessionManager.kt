package com.codingwithmitch.openapi.session

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.codingwithmitch.openapi.interactors.session.CheckPreviousAuthUser
import com.codingwithmitch.openapi.interactors.session.Logout
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.util.PreferenceKeys
import com.codingwithmitch.openapi.util.StateMessage
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.SUCCESS_LOGOUT
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Think of this class as an "application viewmodel"
 * It keeps the authentication state of the user.
 */
@Singleton
class SessionManager
@Inject
constructor(
    private val checkPreviousAuthUser: CheckPreviousAuthUser,
    private val logout: Logout,
    private val preferences: SharedPreferences,
) {

    private val TAG: String = "AppDebug"

    private val sessionScope = CoroutineScope(Main)

    val state: MutableLiveData<SessionState> = MutableLiveData(SessionState())

    init {
        // Check if a user was authenticated in a previous session
        preferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)?.let { email ->
            onTriggerEvent(SessionEvents.CheckPreviousAuthUser(email))
        }
    }

    fun onTriggerEvent(event: SessionEvents){
        when(event){
            is SessionEvents.Login -> {
                login(event.authToken)
            }
            is SessionEvents.Logout -> {
                logout()
            }
            is SessionEvents.CheckPreviousAuthUser -> {
                checkPreviousAuthUser(email = event.email)
            }
        }
    }

    private fun checkPreviousAuthUser(email: String){
        state.value?.let { state ->
            checkPreviousAuthUser.execute(email).onEach { dataState ->
                dataState.data?.let { authToken ->
                    this.state.value = state.copy(authToken = authToken)
                    onTriggerEvent(SessionEvents.Login(authToken))
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.equals(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE)){
                        this.state.value = state.copy(didCheckForPreviousAuthUser = true)
                    }
                    else{
                        appendToMessageQueue(stateMessage)
                    }
                }
            }.launchIn(sessionScope)
        }
    }

    private fun login(authToken: AuthToken){
        state.value?.let { state ->
            if(state.authToken?.token != authToken.token){
                this.state.value = state.copy(authToken = authToken)
            }
        }
    }

    private fun logout(){
        state.value?.let { state ->
            logout.execute().onEach { dataState ->
                dataState.data?.let { response ->
                    if(response.message.equals(SUCCESS_LOGOUT)){
                        this.state.value = state.copy(authToken = null)
                    }
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(sessionScope)
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        // TODO
    }

}















