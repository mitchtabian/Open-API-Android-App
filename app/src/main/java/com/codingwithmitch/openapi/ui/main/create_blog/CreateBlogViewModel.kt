package com.codingwithmitch.openapi.ui.main.create_blog

import android.net.Uri
import androidx.lifecycle.*
import com.codingwithmitch.openapi.repository.main.CreateBlogRepository
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.BaseViewModel
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.Loading
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState
import com.codingwithmitch.openapi.ui.main.create_blog.state.CreateBlogStateEvent
import com.codingwithmitch.openapi.ui.main.create_blog.state.CreateBlogStateEvent.*
import com.codingwithmitch.openapi.ui.main.create_blog.state.CreateBlogViewState
import com.codingwithmitch.openapi.ui.main.create_blog.state.CreateBlogViewState.*
import com.codingwithmitch.openapi.util.AbsentLiveData
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

class CreateBlogViewModel
@Inject
constructor(
    val createBlogRepository: CreateBlogRepository,
    val sessionManager: SessionManager
)
    : BaseViewModel<CreateBlogStateEvent, CreateBlogViewState>()
{


    override fun handleStateEvent(stateEvent: CreateBlogStateEvent): LiveData<DataState<CreateBlogViewState>> {
        when(stateEvent) {
            is CreateNewBlogEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->

                    val title = RequestBody.create(MediaType.parse("text/plain"), stateEvent.title)
                    val body = RequestBody.create(MediaType.parse("text/plain"), stateEvent.body)

                    createBlogRepository.createNewBlogPost(
                        authToken,
                        title,
                        body,
                        stateEvent.image
                    )
                }?: AbsentLiveData.create()
            }

            is None -> {
                return object: LiveData<DataState<CreateBlogViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        value = DataState(null, Loading(false), null)
                    }
                }
            }
        }
    }

    fun setNewBlogFields(title: String?, body: String?, uri: Uri?){
        val update = getCurrentViewStateOrNew()
        val newBlogFields = update.blogFields
        title?.let{ newBlogFields.newBlogTitle = it }
        body?.let{ newBlogFields.newBlogBody = it }
        uri?.let{ newBlogFields.newImageUri = it }
        update.blogFields = newBlogFields
        _viewState.value = update
    }

    fun clearNewBlogFields(){
        val update = getCurrentViewStateOrNew()
        update.blogFields = NewBlogFields()
        _viewState.value = update
    }

    fun getCurrentViewStateOrNew(): CreateBlogViewState {
        val value = viewState.value?.let{
            it
        }?: CreateBlogViewState()
        return value
    }

    fun cancelRequests(){
        createBlogRepository.cancelRequests()
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




















