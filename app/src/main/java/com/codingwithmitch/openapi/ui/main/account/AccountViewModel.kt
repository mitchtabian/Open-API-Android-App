package com.codingwithmitch.openapi.ui.main.account

import android.util.Log
import androidx.lifecycle.*
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.repository.main.AccountRepository
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.main.account.state.AccountDataState
import kotlinx.coroutines.*
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
)
    : ViewModel()
{

    private val TAG: String = "AppDebug"

    private val dataState: MediatorLiveData<AccountDataState> = MediatorLiveData()


    fun observeDataState(): LiveData<AccountDataState>{
        return dataState
    }


    fun saveAccountProperties(email: String, username: String){
        sessionManager.observeSession().value?.authToken?.let { authToken ->
            authToken.account_pk?.let {pk ->
                val source = accountRepository.saveAccountProperties(authToken, AccountProperties(pk, email, username))
                dataState.addSource(source){
                    it.error?.let {
                        dataState.removeSource(source)
                    }
                    it.success?.let {
                        dataState.removeSource(source)
                    }
                    setDataState(it)
                }
            }

        }
    }


    fun getAccountProperties(){
        sessionManager.observeSession().value?.authToken?.let {authToken ->
            val source = accountRepository.getAccountProperties(authToken)
            dataState.addSource(source){
                it.error?.let {
                    dataState.removeSource(source)
                }
                it.success?.let {
                    dataState.removeSource(source)
                }
                setDataState(it)
            }
        }
    }

    fun updatePassword(currentPassword: String, newPassword: String, confirmNewPassword: String){
        sessionManager.observeSession().value?.authToken?.let { authToken ->
            val source = accountRepository.updatePassword(authToken, currentPassword, newPassword, confirmNewPassword)
            dataState.addSource(source){
                it.error?.let {
                    dataState.removeSource(source)
                }
                it.success?.let {
                    dataState.removeSource(source)
                }
                setDataState(it)
            }
        }
    }



    fun setDataState(
        newDataState: AccountDataState? = null
    ){
        viewModelScope.launch(Dispatchers.Main) {

            if(newDataState == null){
                dataState.value = AccountDataState()
            }
            if(dataState.value == null){
                dataState.value = AccountDataState()
            }

            // LOADING
            newDataState?.loading?.let {loading ->
                dataState.value?.let {
                    it.loading = loading
                    dataState.value = it
                }
            }

            // ACCOUNT_PROPERTIES
            newDataState?.accountProperties?.let {accountProperties ->
                dataState.value?.let {
                    it.accountProperties = accountProperties
                    dataState.value = it
                }
            }

            // ERROR
            newDataState?.error?.let {newStateError ->
                dataState.value?.let {
                    it.error = newStateError
                    it.loading = null
                    dataState.value = it
                }
                clearStateMessages()
            }

            // SUCCESS
            newDataState?.success?.let {successResponse ->
                dataState.value?.let {
                    it.loading = null
                    it.success = successResponse
                    dataState.value = it
                }
                clearStateMessages()
            }
        }
    }

    /**
     * Clear SuccessResponse and Error from State.
     * That was if back button is pressed we don't get duplicates
     */
    fun clearStateMessages(){
        dataState.value?.let {
            it.success = null
            it.error = null
            dataState.value = it
        }
    }

    fun logout(){
        sessionManager.logout()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        accountRepository.cancelRequests()
    }
}


















