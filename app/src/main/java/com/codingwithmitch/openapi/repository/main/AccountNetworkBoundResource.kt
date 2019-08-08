package com.codingwithmitch.openapi.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.codingwithmitch.openapi.api.*
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.ui.main.account.state.AccountDataState
import com.codingwithmitch.openapi.util.ErrorHandling
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main


/**
 *  Possibilities for ResponseObject. This is what is returned from account-related Network requests
 *  @see OpenApiMainService
 *  You can either get GenericResponse or AccountProperties
 *  @see GenericResponse
 *  @see AccountProperties
 *
 *  Each scenario must finish with ERROR or SUCCESS to hide the progress bar and complete the transaction
 */
abstract class AccountNetworkBoundResource<ResponseObject>
{

    private val TAG: String = "AppDebug"
    private val ERROR_RESPONSE = "Error"

    private val result = MediatorLiveData<AccountDataState>()

    init {
        setValue(AccountDataState.loading())

        if(isGetRequest()){
            // view cache to start
            val dbSource = loadFromDb()
            result.addSource(dbSource){
                result.removeSource(dbSource)
                setValue(AccountDataState.loading(cachedAccountProperties = it))
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

                            getCoroutineScope().launch(Main) {

                                val job = launch(IO){
                                    updateLocalDb(response.body)
                                }
                                job.join()

                                // finishing by viewing db cache
                                val dbSource = loadFromDb()
                                result.addSource(dbSource){
                                    result.removeSource(dbSource)
                                    setValue(AccountDataState.accountProperties(it))
                                    setValue(AccountDataState.success(null, false))
                                }
                            }
                        }

                        is GenericResponse ->{
                            getCoroutineScope().launch(Main) {

                                val job = launch(IO) {
                                    // can use params passed to method input (in repository) so null is used here
                                    updateLocalDb(null)
                                }
                                job.join()

                                // finishing by viewing db cache
                                val dbSource = loadFromDb()
                                result.addSource(dbSource){
                                    result.removeSource(dbSource)
                                    setValue(AccountDataState.accountProperties(it))
                                    setValue(AccountDataState.success(response.body.response, false))
                                }
                            }

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
        getCoroutineScope().launch(Main) {
            var msg = errorMessage
            var useDialog = true
            if(msg == null){
                msg = "Unknown error"
            }
            else if(ErrorHandling.NetworkErrors.isNetworkError(msg)){
                msg = "Check network connection."
                useDialog = false
            }
            setValue(AccountDataState.error(msg, useDialog))
        }
    }

    fun setValue(accountDataState: AccountDataState){
        getCoroutineScope().launch(Main) {
            result.value = accountDataState
        }
    }


    fun asLiveData() = result as LiveData<AccountDataState>

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    abstract fun loadFromDb(): LiveData<AccountProperties>

    abstract fun updateLocalDb(accountProp: AccountProperties?)

    abstract fun isGetRequest(): Boolean

    abstract fun getCoroutineScope(): CoroutineScope
}
