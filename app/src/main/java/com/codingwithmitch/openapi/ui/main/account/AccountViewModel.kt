package com.codingwithmitch.openapi.ui.main.account

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
                    it.loading?.let {
                        // processing request
                    }
                    it.successResponse?.let {
                        dataState.removeSource(source)
                    }
                    it.accountProperties?.let {
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
                it.loading?.let {
                    // processing request
                }
                it.successResponse?.let {
                    dataState.removeSource(source)
                }
                it.accountProperties?.let {
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
                it.loading?.let {
                    // processing request
                }
                it.successResponse?.let {
                    dataState.removeSource(source)
                }
                it.accountProperties?.let {
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

            newDataState?.error?.let {
                dataState.value = AccountDataState.error(it.errorMessage)
                clearStateMessages()
            }
            newDataState?.loading?.let {
                dataState.value = AccountDataState.loading(it.cachedData)
            }
            newDataState?.successResponse?.let {
                dataState.value = AccountDataState.successResponse(it.message, it.useDialog)
                clearStateMessages()
            }
            newDataState?.accountProperties?.let {
                dataState.value = AccountDataState.accountProperties(it)
            }

            if(newDataState == null){
                dataState.value = AccountDataState()
            }

        }
    }

    /**
     * Clear SuccessResponse and Error from State.
     * That was if back button is pressed we don't get duplicates
     */
    fun clearStateMessages(){
        val currentValue = dataState.value
        currentValue?.successResponse = null
        currentValue?.error = null
        dataState.value = currentValue
    }

    fun logout(){
        sessionManager.logout()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}


















