package com.codingwithmitch.openapi.ui.main.blog.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.openapi.interactors.blog.DeleteBlogPost
import com.codingwithmitch.openapi.interactors.blog.GetBlogFromCache
import com.codingwithmitch.openapi.interactors.blog.IsAuthorOfBlogPost
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.util.MessageType
import com.codingwithmitch.openapi.util.Response
import com.codingwithmitch.openapi.util.StateMessage
import com.codingwithmitch.openapi.util.UIComponentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ViewBlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val getBlogFromCache: GetBlogFromCache,
    private val isAuthorOfBlogPost: IsAuthorOfBlogPost,
    private val deleteBlogPost: DeleteBlogPost,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {

    val state: MutableLiveData<ViewBlogState> = MutableLiveData(ViewBlogState())

    init {
        savedStateHandle.get<Int>("blogPostPk")?.let { blogPostPk ->
            onTriggerEvent(ViewBlogEvents.getBlog(blogPostPk))
        }
    }

    fun onTriggerEvent(event: ViewBlogEvents){
        when(event){
            is ViewBlogEvents.getBlog -> {
                getBlog(
                    event.pk,
                    object: OnCompleteCallback { // Determine if they are the author
                        override fun done() {
                            state.value?.let { state ->
                                state.blogPost?.let { blog ->
                                    onTriggerEvent(ViewBlogEvents.isAuthor(slug = blog.slug))
                                }
                            }
                        }
                    }
                )
            }
            is ViewBlogEvents.isAuthor -> {
                isAuthor(event.slug)
            }
            is ViewBlogEvents.DeleteBlog -> {
                deleteBlog()
            }
            is ViewBlogEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        // TODO
    }

    private fun deleteBlog(){
        state.value?.let { state ->
            state.blogPost?.let { blogPost ->
                deleteBlogPost.execute(
                    authToken = sessionManager.cachedToken.value,
                    blogPost = blogPost
                ).onEach { dataState ->
                    this.state.value = state.copy(isLoading = dataState.isLoading)

                    dataState.data?.let { response ->
                        appendToMessageQueue( // Tell the UI it was deleted
                            stateMessage = StateMessage(
                                response = response
                            )
                        )
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
                authToken = sessionManager.cachedToken.value,
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

















