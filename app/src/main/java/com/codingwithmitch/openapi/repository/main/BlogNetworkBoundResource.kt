package com.codingwithmitch.openapi.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.codingwithmitch.openapi.api.*
import com.codingwithmitch.openapi.api.main.network_responses.BlogListSearchResponse
import com.codingwithmitch.openapi.api.main.network_responses.BlogSearchResponse
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.ui.main.account.state.AccountDataState
import com.codingwithmitch.openapi.ui.main.blog.state.BlogDataState
import com.codingwithmitch.openapi.util.ErrorHandling
import com.codingwithmitch.openapi.util.ErrorHandling.NetworkErrors.Companion.UNABLE_TO_RESOLVE_HOST
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main


/**
 *  Possibilities for ResponseObject. This is what is observed in BlogViewModel
 *  @see BlogDataState
 *  @see OpenApiMainService
 *      1) List<BlogPost>
 *  BlogPost objects are never queried individually
 *
 *  Each scenario must finish with ERROR or SUCCESS to hide the progress bar and complete the transaction
 */
abstract class BlogNetworkBoundResource<ResponseObject>
{

    private val TAG: String = "AppDebug"

    private val result = MediatorLiveData<BlogDataState>()

    init {
        setValue(BlogDataState.loading())

        // view cache to start
        val dbSource = loadFromDb()
        result.addSource(dbSource) {
            result.removeSource(dbSource)
            setValue(BlogDataState.loading(cachedBlogPostList = it))
        }

        // make network call
        val apiResponse = createCall()
        result.addSource(apiResponse){ response ->
            result.removeSource(apiResponse)
            when(response){
                is ApiSuccessResponse ->{
//                    Log.d(TAG, "BlogNetworkBoundResource: ${response.body}")
                    when(response.body){
                        is BlogListSearchResponse ->{

                            getCoroutineScope().launch(Main) {

                                val job = launch(IO){
                                    updateLocalDb(response.body.results)
                                }
                                job.join()

                                // finishing by viewing db cache
                                result.addSource(dbSource){
                                    result.removeSource(dbSource)
                                    setValue(BlogDataState.blogPostList(it))
                                    setValue(BlogDataState.success(null, false))
                                }
                            }
                        }

                        is GenericResponse ->{
                            // TODO("Probably use for updating blog posts")
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
            setValue(BlogDataState.error(msg, useDialog))
        }
    }

    fun setValue(blogDataState: BlogDataState){
        getCoroutineScope().launch(Main)  {
            result.value = blogDataState
        }
    }


    fun asLiveData() = result as LiveData<BlogDataState>

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    abstract fun loadFromDb(): LiveData<List<BlogPost>>

    abstract fun updateLocalDb(blogPostList: List<BlogSearchResponse>?)

    abstract fun getCoroutineScope(): CoroutineScope


}
















