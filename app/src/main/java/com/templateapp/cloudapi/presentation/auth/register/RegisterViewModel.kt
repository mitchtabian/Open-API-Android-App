package com.templateapp.cloudapi.presentation.auth.register

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.templateapp.cloudapi.business.domain.models.Role
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.interactors.account.GetAllRoles
import com.templateapp.cloudapi.business.interactors.auth.Register
import com.templateapp.cloudapi.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
@Inject
constructor(
    private val registerf: Register,
    private val sessionManager: SessionManager,

    private val getAllRoles: GetAllRoles,
) : ViewModel() {
    private val TAG: String = "AppDebug"

    val state: MutableLiveData<RegisterState> = MutableLiveData(RegisterState())
    init {
            onTriggerEvent(RegisterEvents.GetRoles)
    }
    fun onTriggerEvent(event: RegisterEvents) {
        when (event) {
            is RegisterEvents.Registration -> {
                register(
                    email = event.email,
                    role = event.role,
                )
            }

            is RegisterEvents.OnUpdateRole -> {
                onUpdateRole(event.role)
            }
            is RegisterEvents.GetRoles -> {
            getRoles()
            }

            is RegisterEvents.OnUpdateEmail -> {
                onUpdateEmail(event.email)
            }

            is RegisterEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }

            is RegisterEvents.OnUpdateComplete -> {
                onUpdateComplete()
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

    private fun onUpdateEmail(email: String){

        state.value?.let { state ->
            state.register?.let { register ->
                val new = register.copy(email = email)
                this.state.value = state.copy(register = new)
            }
        }
    }

    private fun onUpdateRole(role: Role){
        state.value?.let { state ->
            state.register?.let { register ->
                val new = register.copy(role = role)
                this.state.value = state.copy(register = new)
            }
        }
    }

    private fun onUpdateComplete(){
        state.value?.let { state ->
            this.state.value = state.copy(isComplete = true)
        }
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

    private fun register(
        email: String,
        role: String

    ) {
        // TODO("Perform some simple form validation?")


        state.value?.let { state ->
            registerf.execute(
                email = email,
                role = role

            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { response ->
                    if(response.message == SuccessHandling.RESPONSE_REGISTRATION_MAIL_SENT){

                        onTriggerEvent(RegisterEvents.OnUpdateComplete)
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





































