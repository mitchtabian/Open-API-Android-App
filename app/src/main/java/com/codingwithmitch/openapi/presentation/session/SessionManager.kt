package com.codingwithmitch.openapi.presentation.session

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.codingwithmitch.openapi.business.datasource.datastore.AppDataStore
import com.codingwithmitch.openapi.business.domain.models.AuthToken
import com.codingwithmitch.openapi.business.domain.util.StateMessage
import com.codingwithmitch.openapi.business.domain.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import com.codingwithmitch.openapi.business.domain.util.SuccessHandling.Companion.SUCCESS_LOGOUT
import com.codingwithmitch.openapi.business.domain.util.UIComponentType
import com.codingwithmitch.openapi.business.domain.util.doesMessageAlreadyExistInQueue
import com.codingwithmitch.openapi.business.interactors.session.CheckPreviousAuthUser
import com.codingwithmitch.openapi.business.interactors.session.Logout
import com.codingwithmitch.openapi.presentation.util.DataStoreKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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
    private val appDataStoreManager: AppDataStore,
) {

    private val TAG: String = "AppDebug"

    private val sessionScope = CoroutineScope(Main)

    val state: MutableLiveData<SessionState> = MutableLiveData(SessionState())

    init {
        // Check if a user was authenticated in a previous session
        sessionScope.launch {
            appDataStoreManager.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)?.let { email ->
                onTriggerEvent(SessionEvents.CheckPreviousAuthUser(email))
            }?: onFinishCheckingPrevAuthUser()
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
            is SessionEvents.OnRemoveHeadFromQueue ->{
                removeHeadFromQueue()
            }
        }
    }

    private fun removeHeadFromQueue(){
        state.value?.let { state ->
            try {
                val queue = state.queue
                queue.remove() // can throw exception if empty
                this.state.value = state.copy(queue = queue)
            }catch (e: Exception){
                Log.d(TAG, "removeHeadFromQueue: Nothing to remove from DialogQueue")
            }
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        state.value?.let { state ->
            val queue = state.queue
            if(!stateMessage.doesMessageAlreadyExistInQueue(queue = queue)){
                if(!(stateMessage.response.uiComponentType is UIComponentType.None)){
                    queue.add(stateMessage)
                    this.state.value = state.copy(queue = queue)
                }
            }
        }
    }

    private fun checkPreviousAuthUser(email: String){
        state.value?.let { state ->
            checkPreviousAuthUser.execute(email).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)
                dataState.data?.let { authToken ->
                    this.state.value = state.copy(authToken = authToken)
                    onTriggerEvent(SessionEvents.Login(authToken))
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message.equals(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE)){
                        onFinishCheckingPrevAuthUser()
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
            this.state.value = state.copy(authToken = authToken)
        }
    }

    private fun logout(){
        state.value?.let { state ->
            logout.execute().onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)
                dataState.data?.let { response ->
                    if(response.message.equals(SUCCESS_LOGOUT)){
                        this.state.value = state.copy(authToken = null)
                        clearAuthUser()
                        onFinishCheckingPrevAuthUser()
                    }
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(sessionScope)
        }
    }

    private fun onFinishCheckingPrevAuthUser(){
        state.value?.let { state ->
            this.state.value = state.copy(didCheckForPreviousAuthUser = true)
        }
    }

    private fun clearAuthUser() {
        sessionScope.launch {
            appDataStoreManager.setValue(DataStoreKeys.PREVIOUS_AUTH_USER, "")
        }
    }

}















