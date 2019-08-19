package com.codingwithmitch.openapi.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.codingwithmitch.openapi.api.*
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.util.*
import com.codingwithmitch.openapi.util.Constants.Companion.NETWORK_TIMEOUT
import com.codingwithmitch.openapi.util.Constants.Companion.TESTING_CACHE_DELAY
import com.codingwithmitch.openapi.util.Constants.Companion.TESTING_NETWORK_DELAY
import kotlinx.coroutines.*

abstract class NetworkBoundResource<ResponseObject, CacheObject, ViewStateType>{

    private val TAG: String = "AppDebug"

    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        setCurrentJob(initNewJob())
        setValue(DataState.loading(isLoading = true, cachedData = null))

        if(cancelOperationIfNoInternetConnection()){
            onCompleteJob(DataState.error(ErrorHandling.NetworkErrors.UNABLE_TODO_OPERATION_WO_INTERNET, true))
        }
        else{
            if(shouldLoadFromCache()){
                // view cache to start
                val dbSource = loadFromCache()
                result.addSource(dbSource){
                    result.removeSource(dbSource)
                    setValue(DataState.loading(isLoading = true, cachedData = it))
                }
            }

            if(isNetworkRequest()){
                // make network call
                val apiResponse = createCall()
                result.addSource(apiResponse){ response ->
                    result.removeSource(apiResponse)

                    coroutineScope.launch {
                        doNetworkCall(response)
                    }

                    GlobalScope.launch(Dispatchers.IO) {
                        delay(NETWORK_TIMEOUT)
                        if(!job.isCompleted){
                            Log.e(TAG, "NetworkBoundResource: JOB NETWORK TIMEOUT.")
                            job.cancel(CancellationException(ErrorHandling.NetworkErrors.UNABLE_TO_RESOLVE_HOST))
                        }
                    }
                }
            }
            else{
                coroutineScope.launch {
                    delay(TESTING_CACHE_DELAY)
                    // View data from cache only and return
                    createCacheRequestAndReturn()
                }
            }
        }
    }

    suspend fun doNetworkCall(response: GenericApiResponse<ResponseObject>){

        // simulate network delay for testing
        delay(TESTING_NETWORK_DELAY)
        when(response){
            is ApiSuccessResponse ->{
                handleApiSuccessResponse(response)
            }
            is ApiErrorResponse ->{
                Log.e(TAG, "NetworkBoundResource: ${response.errorMessage}")
                onReturnError(response.errorMessage, true)
            }
            is ApiEmptyResponse ->{
                Log.e(TAG, "NetworkBoundResource: Request returned NOTHING (HTTP 204).")
                onReturnError("HTTP 204. Returned NOTHING.", true)
            }
        }
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>){
        if(job.isActive){
            GlobalScope.launch(Dispatchers.Main) {
                job.complete()
                setValue(dataState)
            }
        }
    }

    fun onReturnError(errorMessage: String?, shouldUseDialog: Boolean){
        if(job.isActive){
            var msg = errorMessage
            var useDialog = shouldUseDialog
            if(msg == null){
                msg = "Unknown error"
            }
            else if(ErrorHandling.NetworkErrors.isNetworkError(msg)){
                msg = "Check network connection."
                useDialog = false
            }
            onCompleteJob(DataState.error(msg, useDialog))
        }
    }

    fun setValue(dataState: DataState<ViewStateType>){
        result.value = dataState
    }

    fun addSourceToResult(source: LiveData<ViewStateType>, removeSource: Boolean){
        result.addSource(source){
            if(removeSource) {
                result.removeSource(source)
            }
            it?.let {
                onCompleteJob(DataState.data(it, null))
            }?: onCompleteJob(DataState.error("Something went wrong. Try restarting the app.", true))
        }
    }

    @UseExperimental(InternalCoroutinesApi::class)
    private fun initNewJob(): Job{
        Log.d(TAG, "initNewJob: called.")
        job = Job() // create new job
        job.invokeOnCompletion(onCancelling = true, invokeImmediately = true, handler = object: CompletionHandler{
            override fun invoke(cause: Throwable?) {
                if(job.isCancelled){
                    Log.e(TAG, "NetworkBoundResource: Job has been cancelled.")
                    cause?.let{
                        onReturnError(it.message, false)
                    }?: onReturnError("Unknown error.", false)
                }
                else if(job.isCompleted){
                    Log.e(TAG, "NetworkBoundResource: Job has been completed.")
                    // Do nothing? Should be handled already
                }
            }
        })
        coroutineScope = CoroutineScope(Dispatchers.IO + job)
        return job
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

    abstract suspend fun createCacheRequestAndReturn()

    abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    abstract fun shouldLoadFromCache(): Boolean

    abstract fun loadFromCache(): LiveData<ViewStateType>

    abstract suspend fun updateLocalDb(cacheObject: CacheObject?)

    abstract fun setCurrentJob(job: Job)

    abstract fun cancelOperationIfNoInternetConnection(): Boolean
    
    abstract fun isNetworkRequest(): Boolean

    
}















