package com.codingwithmitch.openapi.api.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.codingwithmitch.openapi.api.*
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.ui.main.account.state.AccountDataState
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main


abstract class AccountNetworkBoundResource<ResponseType>
{

    private val TAG: String = "AppDebug"
    private val ERROR_RESPONSE = "Error"

    private val result = MediatorLiveData<AccountDataState>()

    init {
        setValue(AccountDataState.Loading(null))

        if(isGetRequest()){
            // view cache to start
            val dbSource = loadFromDb()
            result.addSource(dbSource){
                result.removeSource(dbSource)
                setValue(AccountDataState.Loading(it))
            }
        }

        // make network call
        val apiResponse = createCall()
        result.addSource(apiResponse){ response ->
            result.removeSource(apiResponse)
            when(response){
                is ApiSuccessResponse ->{
                    Log.d(TAG, "AccountNetworkBoundResource: ${response.body}")
                    when(response.body){
                        is AccountProperties ->{

                            // view cache to finish
                            val dbSource = loadFromDb()
                            result.addSource(dbSource){
                                result.removeSource(dbSource)
                                setValue(AccountDataState.Data(it))
                            }
                        }

                        is GenericResponse ->{
                            // insert network result into cache
                            CoroutineScope(IO).launch{
                                updateLocalDb()
                            }
                            setValue(AccountDataState.Data(null))
                        }
                        else -> {
                            onReturnError("Unknown error. Try restarting the app.")
                        }
                    }

                }
                is ApiErrorResponse ->{
                    Log.e(TAG, ": ${response.errorMessage}")
                    onReturnError(response.errorMessage)
                }
                is ApiEmptyResponse ->{
                    Log.e(TAG, ": Request returned NOTHING (HTTP 204).")
                    onReturnError("HTTP 204. Returned NOTHING.")
                }
            }
        }
    }


    fun onReturnError(errorMessage: String?){
        CoroutineScope(Main).launch {
            var msg = errorMessage
            if(msg == null){
                msg = "Unknown error"
            }
            setValue(
                AccountDataState.Error(msg)
            )
        }
    }

    fun setValue(accountDataState: AccountDataState){
        CoroutineScope(Dispatchers.Main)
            .launch {
                result.value = accountDataState
            }
    }


    fun asLiveData() = result as LiveData<AccountDataState>

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseType>>

    abstract fun loadFromDb(): LiveData<AccountProperties>

    abstract fun saveToLocalDb(accountProperties: AccountProperties)

    abstract fun updateLocalDb()

    abstract fun isGetRequest(): Boolean

}
