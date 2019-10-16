package com.codingwithmitch.openapi.ui.main.blog.state

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.repository.main.BlogQueryUtils
import com.codingwithmitch.openapi.repository.main.BlogRepository
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.BaseViewModel
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.Loading
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent.*
import com.codingwithmitch.openapi.util.AbsentLiveData
import com.codingwithmitch.openapi.util.PreferenceKeys
import com.codingwithmitch.openapi.util.PreferenceKeys.Companion.BLOG_FILTER
import com.codingwithmitch.openapi.util.PreferenceKeys.Companion.BLOG_ORDER
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

class BlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val blogRepository: BlogRepository,
    private val sharedPreferences: SharedPreferences
)
    : BaseViewModel<BlogStateEvent, BlogViewState>()
{

    init {
        // set empty list to start
        setBlogListData(ArrayList<BlogPost>())
    }
    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        when(stateEvent){
            is BlogSearchEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.searchBlogPosts(
                        authToken,
                        viewState.value!!.blogFields.searchQuery,
                        viewState.value!!.blogFields.order + viewState.value!!.blogFields.filter,
                        viewState.value!!.blogFields.page
                    )
                }?: AbsentLiveData.create()
            }

            is NextPageEvent -> {
                Log.d(TAG, "BlogViewModel: NextPageEvent detected...")
                if(!viewState.value!!.blogFields.isQueryInProgress
                    && !viewState.value!!.blogFields.isQueryExhausted){
                    Log.d(TAG, "BlogViewModel: Attempting to load next page...")
                    setQueryInProgress(true)
                    incrementPageNumber()
                    return sessionManager.cachedToken.value?.let { authToken ->
                        blogRepository.searchBlogPosts(
                            authToken,
                            viewState.value!!.blogFields.searchQuery,
                            viewState.value!!.blogFields.order + viewState.value!!.blogFields.filter,
                            viewState.value!!.blogFields.page
                        )
                    }?: AbsentLiveData.create()
                }
                else{
                    return AbsentLiveData.create()
                }
            }

            is CheckAuthorOfBlogPost ->{
                Log.d(TAG, "CheckAuthorOfBlogPost: called.")
                if(sessionManager.isConnectedToTheInternet()){
                    return sessionManager.cachedToken.value?.let { authToken ->
                        blogRepository.isAuthorOfBlogPost(
                            authToken,
                            viewState.value!!.viewBlogFields.blogPost!!.slug
                        )
                    }?: AbsentLiveData.create()
                }
                return AbsentLiveData.create()
            }

            is UpdateBlogPostEvent -> {

                return sessionManager.cachedToken.value?.let { authToken ->

                    val title = RequestBody.create(MediaType.parse("text/plain"), stateEvent.title)
                    val body = RequestBody.create(MediaType.parse("text/plain"), stateEvent.body)

                    blogRepository.updateBlogPost(
                        authToken,
                        viewState.value!!.viewBlogFields.blogPost!!.slug,
                        title,
                        body,
                        stateEvent.image
                    )
                }?: AbsentLiveData.create()
            }

            is DeleteBlogPostEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    viewState.value?.let{blogViewState ->
                        blogViewState.viewBlogFields.blogPost?.let { blogPost ->
                            blogRepository.deleteBlogPost(
                                authToken,
                                blogPost
                            )
                        }?: AbsentLiveData.create()
                    }?: AbsentLiveData.create()
                }?: AbsentLiveData.create()
            }

            is None ->{
                return object: LiveData<DataState<BlogViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        value = DataState(null, Loading(false), null)
                    }
                }
            }
        }
    }

    override fun initNewViewState(): BlogViewState {
        return BlogViewState()
    }

    fun loadFirstPage() {
        setQueryInProgress(true)
        setQueryExhausted(false)
        resetPage()
        setBlogFilter(
            sharedPreferences.getString(
                BLOG_FILTER,
                BlogQueryUtils.BLOG_FILTER_DATE_UPDATED
            )
        )
        setBlogOrder(
            sharedPreferences.getString(
                BLOG_ORDER,
                BlogQueryUtils.BLOG_ORDER_DESC
            )
        )
        setStateEvent(BlogSearchEvent())
        Log.e(TAG, "BlogViewModel: loadFirstPage: ${viewState.value!!.blogFields.searchQuery}")
    }

    fun isAuthorOfBlogPost(): Boolean{
        Log.d(TAG, "isAuthorOfBlogPost: ${viewState.value!!.viewBlogFields.isAuthorOfBlogPost}")
        return viewState.value!!.viewBlogFields.isAuthorOfBlogPost
    }

    fun cancelActiveJobs(){
        blogRepository.cancelActiveJobs()
        handlePendingData()
    }

    fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    
}


















