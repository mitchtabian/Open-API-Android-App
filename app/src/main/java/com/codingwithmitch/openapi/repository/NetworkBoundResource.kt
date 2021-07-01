package com.codingwithmitch.openapi.repository

import com.codingwithmitch.openapi.util.*
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.NETWORK_ERROR
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.UNKNOWN_ERROR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@FlowPreview
@Suppress("UNCHECKED_CAST")
abstract class NetworkBoundResource<NetworkObj, CacheObj, ViewState>
constructor(
	private val dispatcher: CoroutineDispatcher,
	private val stateEvent: StateEvent,
	private val apiCall: suspend () -> NetworkObj?,
	private val cacheCall: suspend () -> CacheObj?
) {
	companion object {
		private const val TAG: String = "AppDebug"
	}

	val result: Flow<DataState<out Any?>> = flow {

		// ****** STEP 1: VIEW CACHE ******
		emit(returnCache(markJobComplete = false))

		// ****** STEP 2: MAKE NETWORK CALL, SAVE RESULT TO CACHE ******

		when (val apiResult: ApiResult<NetworkObj> =
			safeApiCall(dispatcher) { apiCall.invoke() } as ApiResult<NetworkObj>) {
			is ApiResult.GenericError -> {
				emit(
					buildError<Flow<DataState<ViewState>>>(
						apiResult.errorMessage ?: UNKNOWN_ERROR,
						UIComponentType.Dialog,
						stateEvent
					)
				)
			}

			is ApiResult.NetworkError -> {
				emit(
					buildError<Flow<DataState<ViewState>>>(
						NETWORK_ERROR,
						UIComponentType.Dialog,
						stateEvent
					)
				)
			}

			is ApiResult.Success -> {
				if (apiResult.value == null) {
					emit(
						buildError<Flow<DataState<ViewState>>>(
							UNKNOWN_ERROR,
							UIComponentType.Dialog,
							stateEvent
						)
					)
				} else {
					updateCache(apiResult.value)
				}
			}
		}

		// ****** STEP 3: VIEW CACHE and MARK JOB COMPLETED ******
		emit(returnCache(markJobComplete = true))
	}

	private suspend fun returnCache(markJobComplete: Boolean): DataState<ViewState> {

		val cacheResult = safeCacheCall(dispatcher) { cacheCall.invoke() }

		var jobCompleteMarker: StateEvent? = null
		if (markJobComplete) {
			jobCompleteMarker = stateEvent
		}

		return object : CacheResponseHandler<ViewState, CacheObj>(
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















