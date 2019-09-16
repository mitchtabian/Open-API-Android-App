package com.codingwithmitch.openapi.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.codingwithmitch.openapi.api.*
import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.api.main.network_responses.BlogCreateUpdateResponse
import com.codingwithmitch.openapi.api.main.network_responses.BlogListSearchResponse
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistence.BlogPostDao
import com.codingwithmitch.openapi.repository.NetworkBoundResource
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.Response
import com.codingwithmitch.openapi.ui.ResponseType
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState.*
import com.codingwithmitch.openapi.util.AbsentLiveData
import com.codingwithmitch.openapi.util.ApiSuccessResponse
import com.codingwithmitch.openapi.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.codingwithmitch.openapi.util.DateUtils
import com.codingwithmitch.openapi.util.ErrorHandling.NetworkErrors.Companion.ERROR_UNKNOWN
import com.codingwithmitch.openapi.util.GenericApiResponse
import com.codingwithmitch.openapi.util.SuccessHandling.NetworkSuccessResponses.Companion.RESPONSE_HAS_PERMISSION_TO_EDIT
import com.codingwithmitch.openapi.util.SuccessHandling.NetworkSuccessResponses.Companion.RESPONSE_NO_PERMISSION_TO_EDIT
import com.codingwithmitch.openapi.util.SuccessHandling.NetworkSuccessResponses.Companion.SUCCESS_BLOG_DELETED
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class BlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
)
{
    private val TAG: String = "AppDebug"

    private var job: Job? = null

    fun searchBlogPosts(authToken: AuthToken, query: String, filterAndOrder: String, page: Int): LiveData<DataState<BlogViewState>> {

        return object: NetworkBoundResource<BlogListSearchResponse, List<BlogPost>, BlogViewState>(){

            override fun isNetworkAvailable(): Boolean {
                Log.d(TAG, "isNetworkAvailable: ${sessionManager.isConnectedToTheInternet()}")
                return sessionManager.isConnectedToTheInternet()
            }

            // if network is down, view cache only and return
            override suspend fun createCacheRequestAndReturn() {
                withContext(Dispatchers.Main){

                    // finishing by viewing db cache
                    result.addSource(loadFromCache()){ viewState ->
                        viewState.blogFields.isQueryInProgress = false
                        if(page * PAGINATION_PAGE_SIZE > viewState.blogFields.blogList.size){
                            viewState.blogFields.isQueryExhausted = true
                        }
                        onCompleteJob(DataState.data(viewState, null))
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogListSearchResponse>) {
                val blogPostList: ArrayList<BlogPost> = ArrayList()
                for(blogPostResponse in response.body.results){
                    blogPostList.add(
                        BlogPost(
                            pk = blogPostResponse.pk,
                            title = blogPostResponse.title,
                            slug = blogPostResponse.slug,
                            body = blogPostResponse.body,
                            image = blogPostResponse.image,
                            date_updated = DateUtils.convertServerStringDateToLong(blogPostResponse.date_updated),
                            username = blogPostResponse.username
                        )
                    )
                }
                updateLocalDb(blogPostList)

                withContext(Dispatchers.Main){

                    // finishing by viewing db cache
                    result.addSource(loadFromCache()){ viewState ->
                        viewState.blogFields.isQueryInProgress = false
                        if(page * PAGINATION_PAGE_SIZE > viewState.blogFields.blogList.size){
                            viewState.blogFields.isQueryExhausted = true
                        }
                        onCompleteJob(DataState.data(viewState, null))
                    }
                }
            }

            override fun shouldLoadFromCache(): Boolean {
                return true
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                return BlogQueryUtils.returnOrderedBlogQuery(
                    blogPostDao = blogPostDao,
                    query = query,
                    filterAndOrder = filterAndOrder,
                    page = page)
                    .switchMap {
                        object: LiveData<BlogViewState>(){
                            override fun onActive() {
                                super.onActive()
                                value = BlogViewState(
                                    BlogFields(
                                        blogList = it, isQueryInProgress = true
                                    )
                                )
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: List<BlogPost>?) {
                // loop through list and update the local db
                if(cacheObject != null){
                    withContext(IO) {
                        for(blogPost in cacheObject){
                            try{
                                // Launch each insert as a separate job to be executed in parallel
                                launch {
                                    Log.d(TAG, "updateLocalDb: inserting blog: ${blogPost}")
                                    blogPostDao.insert(blogPost)
                                }
                            }catch (e: Exception){
                                Log.e(TAG, "updateLocalDb: error updating cache data on blog post with slug: ${blogPost.slug}. " +
                                        "${e.message}")
                                // Could send an error report here or something but I don't think you should throw an error to the UI
                                // Since there could be many blog posts being inserted/updated.
                            }
                        }
                    }
                }
                else{
                    Log.d(TAG, "updateLocalDb: blog post list is null")
                }
            }

            override fun cancelOperationIfNoInternetConnection(): Boolean {
                return false
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogListSearchResponse>> {
                return openApiMainService.searchListBlogPosts(
                    "Token ${authToken.token!!}",
                    query = query,
                    ordering = filterAndOrder,
                    page = page
                )
            }

            override fun setCurrentJob(job: Job) {
                this@BlogRepository.job?.cancel() // cancel existing jobs
                this@BlogRepository.job = job
            }

            override fun isNetworkRequest(): Boolean {
                return true
            }

        }.asLiveData()
    }


    fun isAuthorOfBlogPost(
        authToken: AuthToken,
        slug: String
    ): LiveData<DataState<BlogViewState>> {
        return object: NetworkBoundResource<GenericResponse, Any, BlogViewState>(){

            override fun isNetworkAvailable(): Boolean {
                return sessionManager.isConnectedToTheInternet()
            }

            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                withContext(Dispatchers.Main){

                    Log.d(TAG, "handleApiSuccessResponse: ${response.body.response}")
                    if(response.body.response.equals(RESPONSE_NO_PERMISSION_TO_EDIT)){
                        onCompleteJob(
                            DataState.data(
                                data = BlogViewState(
                                    isAuthorOfBlogPost = false
                                ),
                                response = null
                            )
                        )
                    }
                    else if(response.body.response.equals(RESPONSE_HAS_PERMISSION_TO_EDIT)){
                        onCompleteJob(
                            DataState.data(
                                data = BlogViewState(
                                    isAuthorOfBlogPost = true
                                ),
                                response = null
                            )
                        )
                    }
                    else{
                        onReturnError(ERROR_UNKNOWN, shouldUseDialog = false, shouldUseToast = false)
                    }
                }
            }

            override fun cancelOperationIfNoInternetConnection(): Boolean {
                return !sessionManager.isConnectedToTheInternet()
            }

            // not applicable
            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            // Make an update and change nothing.
            // If they are not the author it will return: "You don't have permission to edit that."
            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.isAuthorOfBlogPost(
                    "Token ${authToken.token!!}",
                    slug
                )
            }

            // not applicable
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            // not applicable
            override fun shouldLoadFromCache(): Boolean {
                return false
            }

            override fun setCurrentJob(job: Job) {
                this@BlogRepository.job?.cancel() // cancel existing jobs
                this@BlogRepository.job = job
            }

            override fun isNetworkRequest(): Boolean {
                return true
            }

        }.asLiveData()
    }


    fun deleteBlogPost(
        authToken: AuthToken,
        blogPost: BlogPost
    ): LiveData<DataState<BlogViewState>>{
        return object: NetworkBoundResource<GenericResponse, BlogPost, BlogViewState>(){

            override fun isNetworkAvailable(): Boolean {
                return sessionManager.isConnectedToTheInternet()
            }

            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {

                if(response.body.response == SUCCESS_BLOG_DELETED){
                    updateLocalDb(blogPost)
                }
                else{
                    onCompleteJob(
                        DataState.error(
                            Response(
                                ERROR_UNKNOWN,
                                ResponseType.Dialog()
                            )
                        )
                    )
                }
            }

            override fun cancelOperationIfNoInternetConnection(): Boolean {
                return !sessionManager.isConnectedToTheInternet()
            }

            // not applicable
            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.deleteBlogPost(
                    "Token ${authToken.token!!}",
                    blogPost.slug
                )
            }

            override suspend fun updateLocalDb(cacheObject: BlogPost?) {
                cacheObject?.let{blogPost ->
                    blogPostDao.deleteBlogPost(blogPost)
                    onCompleteJob(
                        DataState.data(
                            null,
                            Response(SUCCESS_BLOG_DELETED, ResponseType.Toast())
                        )
                    )
                }
            }

            // not applicable
            override fun shouldLoadFromCache(): Boolean {
                return false
            }

            override fun setCurrentJob(job: Job) {
                this@BlogRepository.job?.cancel() // cancel existing jobs
                this@BlogRepository.job = job
            }

            override fun isNetworkRequest(): Boolean {
                return true
            }

        }.asLiveData()
    }

    fun updateBlogPost(
        authToken: AuthToken,
        slug: String,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?
    ): LiveData<DataState<BlogViewState>> {
        return object: NetworkBoundResource<BlogCreateUpdateResponse, BlogPost, BlogViewState>(){

            override fun isNetworkAvailable(): Boolean {
                return sessionManager.isConnectedToTheInternet()
            }

            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogCreateUpdateResponse>) {

                val updatedBlogPost = BlogPost(
                    response.body.pk,
                    response.body.title,
                    response.body.slug,
                    response.body.body,
                    response.body.image,
                    DateUtils.convertServerStringDateToLong(response.body.date_updated),
                    response.body.username
                )
                updateLocalDb(updatedBlogPost)
                withContext(Dispatchers.Main){
                    // finish with success response
                    onCompleteJob(
                        DataState.data(
                            BlogViewState(blogPost = updatedBlogPost),
                            Response(response.body.response, ResponseType.Toast())
                        ))
                }
            }

            override fun cancelOperationIfNoInternetConnection(): Boolean {
                return !sessionManager.isConnectedToTheInternet()
            }

            // not applicable
            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogCreateUpdateResponse>> {
                return openApiMainService.updateBlog(
                    "Token ${authToken.token!!}",
                    slug,
                    title,
                    body,
                    image
                )
            }

            override suspend fun updateLocalDb(cacheObject: BlogPost?) {
                cacheObject?.let{blogPost ->
                    blogPostDao.updateBlogPost(
                        blogPost.pk,
                        blogPost.title,
                        blogPost.body,
                        blogPost.image
                    )
                }
            }

            // not applicable
            override fun shouldLoadFromCache(): Boolean {
                return false
            }

            override fun setCurrentJob(job: Job) {
                this@BlogRepository.job?.cancel() // cancel existing jobs
                this@BlogRepository.job = job
            }

            override fun isNetworkRequest(): Boolean {
                return true
            }

        }.asLiveData()
    }

    fun cancelRequests(){
        Log.d(TAG, "BlogRepository: cancelling requests... ")
        job?.cancel()
    }

}


























