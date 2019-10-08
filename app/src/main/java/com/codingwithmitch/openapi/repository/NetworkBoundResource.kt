package com.codingwithmitch.openapi.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.Response
import com.codingwithmitch.openapi.ui.ResponseType
import com.codingwithmitch.openapi.util.*
import com.codingwithmitch.openapi.util.Constants.Companion.NETWORK_TIMEOUT
import com.codingwithmitch.openapi.util.Constants.Companion.TESTING_CACHE_DELAY
import com.codingwithmitch.openapi.util.Constants.Companion.TESTING_NETWORK_DELAY
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.ERROR_UNKNOWN
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

abstract class NetworkBoundResource<ResponseObject, CacheObject, ViewStateType>
    (
    val methodName: String, // name of job (each method denotes a job)
    isNetworkAvailable: Boolean, // is their a network connection?
    isNetworkRequest: Boolean, // does this job include a network request?
    shouldCancelIfNoInternet: Boolean, // should this job be cancelled if there is no network?
    shouldLoadFromCache: Boolean // should the cached data be loaded?
) {

    private val TAG: String = "AppDebug"

    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        setJob(initNewJob())
        setValue(DataState.loading(isLoading = true, cachedData = null))

        if(!isNetworkAvailable && shouldCancelIfNoInternet){
            onCompleteJob(DataState.error(Response(ErrorHandling.UNABLE_TODO_OPERATION_WO_INTERNET, ResponseType.Dialog())))
        }
        else{
            if(shouldLoadFromCache){
                // view cache to start
                val dbSource = loadFromCache()
                result.addSource(dbSource){
                    result.removeSource(dbSource)
                    setValue(DataState.loading(isLoading = true, cachedData = it))
                }
            }

            if(isNetworkRequest && isNetworkAvailable){
                coroutineScope.launch {
                    // simulate network delay for testing
                    delay(TESTING_NETWORK_DELAY)

                    withContext(Main){
                        // make network call
                        val apiResponse = createCall()
                        result.addSource(apiResponse){ response ->
                            result.removeSource(apiResponse)

                            coroutineScope.launch{
                                handleNetworkCall(response)
                            }
                        }
                    }
                }
                GlobalScope.launch(IO) {
                    delay(NETWORK_TIMEOUT)
                    if(!job.isCompleted){
                        Log.e(TAG, "NetworkBoundResource: JOB NETWORK TIMEOUT.")
                        job.cancel(CancellationException(ErrorHandling.UNABLE_TO_RESOLVE_HOST))
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

    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>){

        when(response){
            is ApiSuccessResponse ->{
                handleApiSuccessResponse(response)
            }
            is ApiErrorResponse ->{
                Log.e(TAG, "NetworkBoundResource: ${response.errorMessage}")
                onErrorReturn(response.errorMessage, true, false)
            }
            is ApiEmptyResponse ->{
                Log.e(TAG, "NetworkBoundResource: Request returned NOTHING (HTTP 204).")
                onErrorReturn("HTTP 204. Returned NOTHING.", true, false)
            }
        }
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>){
        Log.d(TAG, "onCompleteJob: called...")
        GlobalScope.launch(Main) {
            job.complete()
            setValue(dataState)
        }
    }

    fun onErrorReturn(errorMessage: String?, shouldUseDialog: Boolean, shouldUseToast: Boolean){
        var msg = errorMessage
        var useDialog = shouldUseDialog
        var responseType: ResponseType = ResponseType.None()
        if(msg == null){
            msg = ERROR_UNKNOWN
        }
        else if(ErrorHandling.isNetworkError(msg)){
            msg = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }
        if(shouldUseToast){
            responseType = ResponseType.Toast()
        }
        if(useDialog){
            responseType = ResponseType.Dialog()
        }

        onCompleteJob(DataState.error(Response(msg, responseType)))
    }

    fun setValue(dataState: DataState<ViewStateType>){
        result.value = dataState
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
                        onErrorReturn(it.message, false, true)
                    }?: onErrorReturn("Unknown error.", false, true)
                }
                else if(job.isCompleted){
                    Log.e(TAG, "NetworkBoundResource: Job has been completed.")
                    // Do nothing? Should be handled already
                }
            }
        })
        coroutineScope = CoroutineScope(IO + job)
        return job
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

    abstract suspend fun createCacheRequestAndReturn()

    abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    abstract fun loadFromCache(): LiveData<ViewStateType>

    abstract suspend fun updateLocalDb(cacheObject: CacheObject?)

    abstract fun setJob(job: Job)

}















