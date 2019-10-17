package com.codingwithmitch.openapi.ui.main.blog.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
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
    sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
)
    : BaseViewModel<BlogStateEvent, BlogViewState>()
{

    init {
        // set empty list to start
        setBlogListData(ArrayList<BlogPost>())

        setBlogFilter(
            sharedPreferences.getString(
                BLOG_FILTER,
                BlogQueryUtils.BLOG_FILTER_DATE_UPDATED
            )
        )
        setBlogOrder(
            sharedPreferences.getString(
                BLOG_ORDER,
                BlogQueryUtils.BLOG_ORDER_ASC
            )
        )
    }

    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        when(stateEvent){
            is BlogSearchEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.searchBlogPosts(
                        authToken,
                        viewState.value!!.blogFields.searchQuery,
                        viewState.value!!.blogFields.order
                                + viewState.value!!.blogFields.filter,
                        viewState.value!!.blogFields.page
                    )
                }?: AbsentLiveData.create()
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

    fun getFilter(): String? {
       getCurrentViewStateOrNew().let {
           return it.blogFields.filter
       }
    }

    fun getOrder(): String {
        getCurrentViewStateOrNew().let {
            return it.blogFields.order
        }
    }

    fun isAuthorOfBlogPost(): Boolean{
        getCurrentViewStateOrNew().let {
            return it.viewBlogFields.isAuthorOfBlogPost
        }
    }

    fun saveFilterOptions(filter: String, order: String){
        editor.putString(BLOG_FILTER, filter)
        editor.apply()

        editor.putString(BLOG_ORDER, order)
        editor.apply()
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


















