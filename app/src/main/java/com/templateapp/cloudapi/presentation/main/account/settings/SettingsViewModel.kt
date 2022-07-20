package com.templateapp.cloudapi.presentation.main.account.settings

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.templateapp.cloudapi.R
import androidx.navigation.fragment.findNavController
import com.templateapp.cloudapi.business.domain.util.StateMessage
import com.templateapp.cloudapi.business.domain.util.SuccessHandling
import com.templateapp.cloudapi.business.domain.util.UIComponentType
import com.templateapp.cloudapi.business.domain.util.doesMessageAlreadyExistInQueue
import com.templateapp.cloudapi.business.interactors.account.GetAccount
import com.templateapp.cloudapi.presentation.main.account.update.UpdateAccountEvents
import com.templateapp.cloudapi.presentation.main.account.users.ManageUsersFragment
import com.templateapp.cloudapi.presentation.session.SessionEvents
import com.templateapp.cloudapi.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val getAccount: GetAccount,
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<SettingsState> = MutableLiveData(SettingsState())

    fun onTriggerEvent(event: SettingsEvents) {
        when (event) {
            is SettingsEvents.GetAccount -> {
                getAccount()
            }
            is SettingsEvents.Logout -> {
                logout()
            }
            is SettingsEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }

        }
    }


    fun checkAdminRole(): Boolean {
        var isAdmin = false;
        state.value?.let { state ->
            getAccount.execute(
                authToken = sessionManager.state.value?.authToken,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { account ->
                    this.state.value = state.copy(account = account)

                    if(account.role.title == "Admin"){
                        isAdmin = true;
                    }
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
        return isAdmin;
    }

    private fun removeHeadFromQueue() {
        state.value?.let { state ->
            try {
                val queue = state.queue
                queue.remove() // can throw exception if empty
                this.state.value = state.copy(queue = queue)
            } catch (e: Exception) {
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

    private fun getAccount() {

        state.value?.let { state ->
            getAccount.execute(
                authToken = sessionManager.state.value?.authToken,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { account ->
                    this.state.value = state.copy(account = account)

                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }

    }

    private fun logout() {
        sessionManager.onTriggerEvent(SessionEvents.Logout)
    }

    private fun manageUsers() {
        //manageUsersFragment.onCreateView();

    }

}















