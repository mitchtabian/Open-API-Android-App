package com.codingwithmitch.openapi.ui.main.blog

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.repository.main.BlogQueryUtils
import com.codingwithmitch.openapi.repository.main.BlogRepository
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.BaseViewModel
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.Loading
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent.*
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState
import com.codingwithmitch.openapi.util.AbsentLiveData
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
                        viewState.value!!.searchQuery,
                        viewState.value!!.order + viewState.value!!.filter,
                        viewState.value!!.page
                    )
                }?: AbsentLiveData.create()
            }

            is NextPageEvent -> {
             Log.d(TAG, "BlogViewModel: NextPageEvent detected...")
             return sessionManager.cachedToken.value?.let { authToken ->
                 blogRepository.searchBlogPosts(
                     authToken,
                     viewState.value!!.searchQuery,
                     viewState.value!!.order + viewState.value!!.filter,
                     viewState.value!!.page
                 )
             }?: AbsentLiveData.create()
            }

            is CheckAuthorOfBlogPost ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.getAccountProperties(authToken)
                }?: AbsentLiveData.create()
            }

            is UpdateBlogPostEvent -> {

                return sessionManager.cachedToken.value?.let { authToken ->

                    val title = RequestBody.create(MediaType.parse("text/plain"), stateEvent.title)
                    val body = RequestBody.create(MediaType.parse("text/plain"), stateEvent.body)

                    blogRepository.updateBlogPost(
                        authToken,
                        viewState.value!!.blogPost!!.slug,
                        title,
                        body,
                        stateEvent.image
                    )
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

    fun loadInitialBlogs(){
        // if the user hasn't made a query yet, show some blogs
        val value = getCurrentViewStateOrNew()
        if(value.blogList.size == 0){
            loadFirstPage("")
        }
    }

    fun loadFirstPage(query: String) {
        setQueryInProgress(true)
        setQueryExhausted(false)
        resetPage()
        setBlogFilter(
            sharedPreferences.getString(BLOG_FILTER, BlogQueryUtils.BLOG_FILTER_DATE_UPDATED)
        )
        setBlogOrder(
            sharedPreferences.getString(BLOG_ORDER, BlogQueryUtils.BLOG_FILTER_DATE_UPDATED)
        )
        setQuery(query)
        setStateEvent(BlogSearchEvent())
        Log.e(TAG, "BlogViewModel: loadFirstPage: ${viewState.value?.page}")
    }

    fun loadNextPage(){
        if(!viewState.value!!.isQueryInProgress && !viewState.value!!.isQueryExhausted){
            Log.d(TAG, "BlogViewModel: Attempting to load next page...")
            setQueryInProgress(true)
            incrementPageNumber()
            setStateEvent(NextPageEvent())
        }
    }

    fun resetPage(){
        val update = getCurrentViewStateOrNew()
        update.page = 1
        _viewState.value = update
    }

    fun setQuery(query: String){
        val update = getCurrentViewStateOrNew()
        update.searchQuery = query
        _viewState.value = update
    }

    fun setBlogListData(blogList: List<BlogPost>){
        val update = getCurrentViewStateOrNew()
        update.blogList = blogList
        _viewState.value = update
    }

    fun incrementPageNumber(){
        val update = getCurrentViewStateOrNew()
        val page = update.copy().page
        update.page = page + 1
        _viewState.value = update
    }

    fun setQueryExhausted(isExhausted: Boolean){
        val update = getCurrentViewStateOrNew()
        update.isQueryExhausted = isExhausted
        _viewState.value = update
    }

    fun setQueryInProgress(isInProgress: Boolean){
        val update = getCurrentViewStateOrNew()
        update.isQueryInProgress = isInProgress
        _viewState.value = update
    }

    // Filter can be "date_updated" or "username"
    fun setBlogFilter(filter: String?){
        filter?.let{
            val update = getCurrentViewStateOrNew()
            update.filter = filter
            _viewState.value = update
        }
    }

    // Order can be "-" or ""
    // Note: "-" = DESC, "" = ASC
    fun setBlogOrder(order: String){
        val update = getCurrentViewStateOrNew()
        update.order = order
        _viewState.value = update
    }

    fun setBlogPost(blogPost: BlogPost){
        val update = getCurrentViewStateOrNew()
        update.blogPost = blogPost
        _viewState.value = update
    }

    fun setAccountProperties(accountProperties: AccountProperties){
        val update = getCurrentViewStateOrNew()
        update.accountProperties = accountProperties
        _viewState.value = update
    }

    fun updateListItem(newBlogPost: BlogPost){
        val update = getCurrentViewStateOrNew()
        val list = update.blogList.toMutableList()
        for(i in 0..list.size - 1){
            if(list[i] == newBlogPost){
               list[i] = newBlogPost
            }
        }
        update.blogList = list
        _viewState.value = update
    }

    fun setUpdatedBlogFields(title: String?, body: String?, uri: Uri?){
        val update = getCurrentViewStateOrNew()
        val updatedBlogFields = update.updatedBlogFields
        title?.let{ updatedBlogFields.updatedBlogTitle = it }
        body?.let{ updatedBlogFields.updatedBlogBody = it }
        uri?.let{ updatedBlogFields.updatedImageUri = it }
        update.updatedBlogFields = updatedBlogFields
        _viewState.value = update
    }

    fun isAuthorOfBlogPost(): Boolean{
        val blogPostAuthorUsername = viewState.value?.let {
            it.blogPost?.username
        }
        val accountProperties = viewState.value?.let{
            it.accountProperties?.let {
                it
            }
        }
        return blogPostAuthorUsername.equals(accountProperties?.username)
    }

    fun getCurrentViewStateOrNew(): BlogViewState{
        val value = viewState.value?.let{
            it
        }?: BlogViewState()
        return value
    }


    fun cancelRequests(){
        blogRepository.cancelRequests()
        handlePendingData()
    }

    fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelRequests()
    }

    
}


















