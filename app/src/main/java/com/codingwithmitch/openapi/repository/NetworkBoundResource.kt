package com.codingwithmitch.openapi.repository

import android.util.Log
import com.codingwithmitch.openapi.util.*
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.NETWORK_ERROR
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.UNKNOWN_ERROR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


@FlowPreview
abstract class NetworkBoundResource<NetworkObj, CacheObj, ViewState>
constructor(
    private val dispatcher: CoroutineDispatcher,
    private val stateEvent: StateEvent,
    private val apiCall: suspend () -> NetworkObj?,
    private val cacheCall: suspend () -> CacheObj?
)
{

    private val TAG: String = "AppDebug"

    val result: Flow<DataState<ViewState>> = flow{

        // ****** STEP 1: VIEW CACHE ******
        emit(returnCache(markJobComplete = false))

        // ****** STEP 2: MAKE NETWORK CALL, SAVE RESULT TO CACHE ******
        val apiResult = safeApiCall(dispatcher){apiCall.invoke()}

        when(apiResult){
            is ApiResult.GenericError -> {
                emit(
                    buildError(
                        apiResult.errorMessage?.let { it }?: UNKNOWN_ERROR,
                        UIComponentType.Dialog(),
                        stateEvent
                    )
                )
            }

            is ApiResult.NetworkError -> {
                emit(
                    buildError(
                        NETWORK_ERROR,
                        UIComponentType.Dialog(),
                        stateEvent
                    )
                )
            }

            is ApiResult.Success -> {
                if(apiResult.value == null){
                    emit(
                        buildError(
                            UNKNOWN_ERROR,
                            UIComponentType.Dialog(),
                            stateEvent
                        )
                    )
                }
                else{
                    updateCache(apiResult.value as NetworkObj)
                }
            }
        }

        // ****** STEP 3: VIEW CACHE and MARK JOB COMPLETED ******
        emit(returnCache(markJobComplete = true))
    }

    private suspend fun returnCache(markJobComplete: Boolean): DataState<ViewState> {
        
        val cacheResult = safeCacheCall(dispatcher){cacheCall.invoke()}

        var jobCompleteMarker: StateEvent? = null
        if(markJobComplete){
            jobCompleteMarker = stateEvent
        }

        return object: CacheResponseHandler<ViewState, CacheObj>(
            response = cacheResult,
            stateEvent = jobCompleteMarker
        ) {
            override suspend fun handleSuccess(resultObj: CacheObj): DataState<ViewState> {
                return handleCacheSuccess(resultObj)
            }
        }.getResult()

    }

    abstract suspend fun updateCache(networkObject: NetworkObj)

    abstract fun handleCacheSuccess(resultObj: CacheObj): DataState<ViewState> // make sure to return null for stateEvent


}















