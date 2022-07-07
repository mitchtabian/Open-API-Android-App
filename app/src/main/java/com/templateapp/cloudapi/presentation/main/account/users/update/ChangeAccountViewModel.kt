package com.templateapp.cloudapi.presentation.main.account.users.update

import android.content.Context
import android.os.DropBoxManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.domain.models.Role
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.interactors.account.ChangeAccount
import com.templateapp.cloudapi.business.interactors.account.GetAccountFromCache
import com.templateapp.cloudapi.business.interactors.account.GetAllRoles
import com.templateapp.cloudapi.business.interactors.account.GetAllUsers
import com.templateapp.cloudapi.presentation.main.account.users.ManageUsersEvents
import com.templateapp.cloudapi.presentation.session.SessionManager
import dagger.Provides
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ChangeAccountViewModel
@Inject
constructor(
    public val sessionManager: SessionManager,
    private val changeAccount: ChangeAccount,
    private val getAccountFromCache: GetAccountFromCache,
    public val getAllRoles: GetAllRoles,
    savedStateHandle: SavedStateHandle,
): ViewModel(){

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<ChangeAccountState> = MutableLiveData(ChangeAccountState())

    init {
        savedStateHandle.get<String>("accountId")?.let { accountId ->
            onTriggerEvent(ChangeAccountEvents.GetAccountFromCache(accountId))
        }
        onTriggerEvent(ChangeAccountEvents.GetRoles)


    }
    fun onTriggerEvent(event: ChangeAccountEvents){

        when(event){
            is ChangeAccountEvents.OnUpdateEmail -> {
                onUpdateEmail(event.email)
            }
            is ChangeAccountEvents.OnUpdateUsername -> {
                onUpdateUsername(event.username)
            }
            is ChangeAccountEvents.OnUpdateRole -> {
                onUpdateRole(event.role)
            }
            is ChangeAccountEvents.OnUpdateAge -> {
                onUpdateAge(event.age)
            }
            is ChangeAccountEvents.OnUpdateEnabled -> {
                onUpdateEnabled(event.enabled)
            }
            is ChangeAccountEvents.GetAccountFromCache -> {
                getAccount(event._id)
            }
            is ChangeAccountEvents.Update -> {
                update(
                    email = event.email,
                    username = event.username,
                    age = event.age,
                    enabled = event.enabled,
                    role = event.role,
                    initEmail = event.initEmail,
                    initName = event.initName
                )
            }
            is ChangeAccountEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
            is ChangeAccountEvents.OnUpdateComplete -> {
                onUpdateComplete()
            }

            is ChangeAccountEvents.GetRoles -> {
                getRoles()
            }
        }
    }

    public fun getRoles() : List<Role> {
        var lista : List<Role> = emptyList()
        state.value?.let { state ->
            getAllRoles.execute(
                authToken = sessionManager.state.value?.authToken,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    this.state.value = state.copy(roles = list)
                }
                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){
                        //onUpdateQueryExhausted(true)
                    }else{
                        appendToMessageQueue(stateMessage)
                    }
                }

            }.launchIn(viewModelScope)
        }
        return lista;
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

    private fun onUpdateComplete(){
        state.value?.let { state ->
            this.state.value = state.copy(isUpdateComplete = true)
        }
    }

    private fun onUpdateEmail(email: String){

        state.value?.let { state ->
            state.account?.let { account ->
                val new = account.copy(email = email)
                this.state.value = state.copy(account = new)
            }
        }
    }
    private fun onUpdateEnabled(enabled: Boolean){
        state.value?.let { state ->
            state.account?.let { account ->
                val new = account.copy(enabled = enabled)
                this.state.value = state.copy(account = new)
            }
        }
    }

    private fun onUpdateAge(age: Int){
        state.value?.let { state ->
            state.account?.let { account ->
                val new = account.copy(age = age)
                this.state.value = state.copy(account = new)
            }
        }
    }

    private fun onUpdateUsername(name: String){
        state.value?.let { state ->
            state.account?.let { account ->
                val new = account.copy(name = name)
                this.state.value = state.copy(account = new)
            }
        }
    }

    private fun onUpdateRole(role: Role){
        state.value?.let { state ->
            state.account?.let { account ->
                val new = account.copy(role = role)
                this.state.value = state.copy(account = new)
            }
        }
    }

    private fun getAccount(id: String) {

        state.value?.let { state ->
            getAccountFromCache.execute(
                _id = id,
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

    private fun update(email: String, username: String, age: Int, enabled: Boolean, role: String, initEmail: String, initName: String){
        state.value?.let { state ->
            changeAccount.execute(
                authToken = sessionManager.state.value?.authToken,
                _id = sessionManager.state.value?.authToken?.accountId,
                email = email,
                name = username,
                age = age,
                enabled = enabled,
                role = role,
                initEmail = initEmail,
                initName = initName
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { response ->
                    if(response.message == SuccessHandling.SUCCESS_ACCOUNT_UPDATED){

                        onTriggerEvent(ChangeAccountEvents.OnUpdateComplete)
                    }else{

                        appendToMessageQueue(
                            stateMessage = StateMessage(
                                response = response
                            )
                        )
                    }
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

}


















