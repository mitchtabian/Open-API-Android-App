package com.codingwithmitch.openapi.presentation.main.blog.detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.openapi.business.domain.util.StateMessage
import com.codingwithmitch.openapi.business.domain.util.SuccessHandling
import com.codingwithmitch.openapi.business.domain.util.UIComponentType
import com.codingwithmitch.openapi.business.domain.util.doesMessageAlreadyExistInQueue
import com.codingwithmitch.openapi.business.interactors.blog.ConfirmBlogExistsOnServer
import com.codingwithmitch.openapi.business.interactors.blog.DeleteBlogPost
import com.codingwithmitch.openapi.business.interactors.blog.GetBlogFromCache
import com.codingwithmitch.openapi.business.interactors.blog.IsAuthorOfBlogPost
import com.codingwithmitch.openapi.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

const val SHOULD_REFRESH = "should_refresh"

@HiltViewModel
class ViewBlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val getBlogFromCache: GetBlogFromCache,
    private val confirmBlogExistsOnServer: ConfirmBlogExistsOnServer,
    private val isAuthorOfBlogPost: IsAuthorOfBlogPost,
    private val deleteBlogPost: DeleteBlogPost,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<ViewBlogState> = MutableLiveData(ViewBlogState())

    init {
        savedStateHandle.get<Int>("blogPostPk")?.let { blogPostPk ->
            onTriggerEvent(ViewBlogEvents.GetBlog(blogPostPk))
        }
    }

    fun onTriggerEvent(event: ViewBlogEvents){
        when(event){
            is ViewBlogEvents.GetBlog -> {
                getBlog(
                    event.pk,
                    object: OnCompleteCallback { // Determine if blog exists on server
                        override fun done() {
                            state.value?.let { state ->
                                state.blogPost?.let { blog ->
                                    onTriggerEvent(ViewBlogEvents.ConfirmBlogExistsOnServer(pk = event.pk, blog.slug))
                                }
                            }
                        }
                    }
                )
            }
            is ViewBlogEvents.ConfirmBlogExistsOnServer -> {
                confirmBlogExistsOnServer(
                    event.pk,
                    event.slug,
                    object: OnCompleteCallback { // Determine if they are the author
                        override fun done() {
                            state.value?.let { state ->
                                state.blogPost?.let { blog ->
                                    onTriggerEvent(ViewBlogEvents.IsAuthor(slug = blog.slug))
                                }
                            }
                        }
                    }
                )
            }
            is ViewBlogEvents.Refresh ->{
                refresh()
            }
            is ViewBlogEvents.IsAuthor -> {
                isAuthor(event.slug)
            }
            is ViewBlogEvents.DeleteBlog -> {
                deleteBlog()
            }
            is ViewBlogEvents.OnDeleteComplete ->{
                onDeleteComplete()
            }
            is ViewBlogEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is ViewBlogEvents.OnRemoveHeadFromQueue ->{
                removeHeadFromQueue()
            }
        }
    }

    private fun onDeleteComplete() {
        state.value?.let { state ->
            this.state.value = state.copy(isDeleteComplete = true)
        }
    }

    private fun removeHeadFromQueue(){
        state.value?.let { state ->
            try {
                val queue = state.queue
                queue.remove() // can throw exception if empty
                this.state.value = state.copy(queue = queue)
            }catch (e: Exception){
                Log.d(TAG, "removeHeadFromQueue: Nothing to remove from DialogQueue")
            }
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        state.value?.let { state ->
            val queue = state.queue
            if(!stateMessage.doesMessageAlreadyExistInQueue(queue = queue)){
                if(!(stateMessage.response.uiComponentType is UIComponentType.None)){
                    queue.add(stateMessage)
                    this.state.value = state.copy(queue = queue)
                }
            }
        }
    }

    private fun refresh(){
        state.value?.let { state ->
            state.blogPost?.let { blogPost ->
                getBlog(
                    pk = blogPost.pk,
                    callback = object: OnCompleteCallback{
                        override fun done() {
                            // do nothing
                        }
                    }
                )
            }
        }
    }

    private fun confirmBlogExistsOnServer(pk: Int, slug: String, callback: OnCompleteCallback){
        state.value?.let { state ->
            confirmBlogExistsOnServer.execute(
                authToken = sessionManager.state.value?.authToken,
                pk = pk,
                slug = slug,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { response ->
                    if(response.message == SuccessHandling.SUCCESS_BLOG_DOES_NOT_EXIST_IN_CACHE
                        || response.message == SuccessHandling.SUCCESS_BLOG_EXISTS_ON_SERVER
                    ){
                        // Blog exists in cache and on server. All is good.
                        callback.done()
                    }else{
                        appendToMessageQueue(
                            stateMessage = StateMessage(
                                response = response
                            )
                        )
                    }
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun deleteBlog(){
        state.value?.let { state ->
            state.blogPost?.let { blogPost ->
                deleteBlogPost.execute(
                    authToken = sessionManager.state.value?.authToken,
                    blogPost = blogPost
                ).onEach { dataState ->
                    this.state.value = state.copy(isLoading = dataState.isLoading)

                    dataState.data?.let { response ->
                        if(response.message == SuccessHandling.SUCCESS_BLOG_DELETED){
                            onTriggerEvent(ViewBlogEvents.OnDeleteComplete)
                        }else{
                            appendToMessageQueue(
                                stateMessage = StateMessage(
                                    response = response
                                )
                            )
                        }
                    }

                    dataState.stateMessage?.let { stateMessage ->
                        appendToMessageQueue(stateMessage)
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    /**
     * @param callback: If the blog post is successfully retrieved from cache, execute to determine if the authenticated user is the author.
     */
    private fun getBlog(pk: Int, callback: OnCompleteCallback){
        state.value?.let { state ->
            getBlogFromCache.execute(
                pk = pk
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { blog ->
                    this.state.value = state.copy(blogPost = blog)
                    callback.done()
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun isAuthor(slug: String){
        state.value?.let { state ->
            isAuthorOfBlogPost.execute(
                authToken = sessionManager.state.value?.authToken,
                slug = slug,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { isAuthor ->
                    this.state.value = state.copy(isAuthor = isAuthor)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }


}


interface OnCompleteCallback {
    fun done()
}

















