package com.codingwithmitch.openapi.ui.main.account

import android.util.Log
import androidx.lifecycle.*
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.repository.main.AccountRepository
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.main.account.state.AccountDataState
import com.codingwithmitch.openapi.ui.main.account.state.AccountViewState
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
    private val viewState: MutableLiveData<AccountViewState> = MutableLiveData()

    init {
        setViewState(message = null)
    }

    fun observeDataState(): LiveData<AccountDataState>{
        return dataState
    }

    fun observeViewState(): LiveData<AccountViewState>{
        return viewState
    }


    fun saveAccountProperties(email: String, username: String){
        sessionManager.observeSession().value?.authToken?.let { authToken ->
            authToken.account_pk?.let {pk ->
                val source = accountRepository.saveAccountProperties(authToken, AccountProperties(pk, email, username))
                dataState.addSource(source){
                    when(it){
                        is AccountDataState.Error -> {
                            dataState.removeSource(source)
                        }
                        is AccountDataState.Data -> {
                            Log.d(TAG, "data: ${it.accountProperties}")
                            setViewState(message = "Saved")
                            dataState.removeSource(source)
                        }
                    }
                    setDataState(data_state = it)
                }
            }

        }
    }


    fun getAccountProperties(){
        sessionManager.observeSession().value?.authToken?.let {authToken ->
            val source = accountRepository.getAccountProperties(authToken)
            dataState.addSource(source){
                when(it){
                    is AccountDataState.Error -> {
                        dataState.removeSource(source)
                    }
                    is AccountDataState.Data -> {
                        dataState.removeSource(source)
                    }
                }
                setDataState(data_state = it)
            }
        }
    }

    fun setDataState(
        data_state: AccountDataState? = null
    ){
        if(data_state != dataState.value){
            viewModelScope.launch(Dispatchers.Main) {
                data_state?.let {
                    dataState.value = it
                }
            }
        }
    }

    fun setViewState(
        // Message
        message: String? = null

    ){
        viewModelScope.launch(Dispatchers.Main) {
            viewState.value?.run {

                viewState.value = AccountViewState(
                    message?.let { AccountViewState.UIMessage(it) }
                )
                Log.d(TAG, "setting msg: ${message}")

                // update the LiveData
                viewState.value = this
            }?: initViewState(viewState)
        }
    }

    fun initViewState(viewState: MutableLiveData<AccountViewState>){
        viewState.value = AccountViewState()
    }

    fun logout(){
        sessionManager.logout()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}


















