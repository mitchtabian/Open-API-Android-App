package com.codingwithmitch.openapi.presentation.main.blog.update

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.openapi.business.domain.util.StateMessage
import com.codingwithmitch.openapi.business.domain.util.SuccessHandling.Companion.SUCCESS_BLOG_UPDATED
import com.codingwithmitch.openapi.business.domain.util.UIComponentType
import com.codingwithmitch.openapi.business.domain.util.doesMessageAlreadyExistInQueue
import com.codingwithmitch.openapi.business.interactors.blog.GetBlogFromCache
import com.codingwithmitch.openapi.business.interactors.blog.UpdateBlogPost
import com.codingwithmitch.openapi.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UpdateBlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val getBlogFromCache: GetBlogFromCache,
    private val updateBlogPost: UpdateBlogPost,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<UpdateBlogState> = MutableLiveData(UpdateBlogState())

    init {
        savedStateHandle.get<Int>("blogPostPk")?.let { blogPostPk ->
            onTriggerEvent(UpdateBlogEvents.getBlog(blogPostPk))
        }
    }

    fun onTriggerEvent(event: UpdateBlogEvents) {
        when(event){
            is UpdateBlogEvents.getBlog -> {
                getBlog(event.pk,)
            }
            is UpdateBlogEvents.OnUpdateUri -> {
                onUpdateImageUri(event.uri)
            }
            is UpdateBlogEvents.OnUpdateTitle -> {
                onUpdateTitle(event.title)
            }
            is UpdateBlogEvents.OnUpdateBody -> {
                onUpdateBody(event.body)
            }
            is UpdateBlogEvents.Update -> {
                update()
            }
            is UpdateBlogEvents.OnUpdateComplete ->{
                onUpdateComplete()
            }
            is UpdateBlogEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is UpdateBlogEvents.OnRemoveHeadFromQueue ->{
                removeHeadFromQueue()
            }
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

    private fun onUpdateComplete(){
        state.value?.let { state ->
            this.state.value = state.copy(isUpdateComplete = true)
        }
    }

    private fun onUpdateTitle(title: String){
        state.value?.let { state ->
            state.blogPost?.let { blogPost ->
                val curr = blogPost.copy(title = title)
                this.state.value = state.copy(blogPost = curr)
            }
        }
    }

    private fun onUpdateBody(body: String){
        state.value?.let { state ->
            state.blogPost?.let { blogPost ->
                val curr = blogPost.copy(body = body)
                this.state.value = state.copy(blogPost = curr)
            }
        }
    }

    private fun onUpdateImageUri(uri: Uri){
        state.value?.let { state ->
            this.state.value = state.copy(newImageUri = uri)
        }
    }

    private fun update(){
        state.value?.let { state ->
            state.blogPost?.let { blogPost ->
                val title = RequestBody.create(
                    MediaType.parse("text/plain"),
                    blogPost.title
                )
                val body = RequestBody.create(
                    MediaType.parse("text/plain"),
                    blogPost.body
                )
                var multipartBody: MultipartBody.Part? = null
                if(state.newImageUri != null){
                    state.newImageUri.path?.let { filePath ->
                        val imageFile = File(filePath)
                        if(imageFile.exists()){
                            val requestBody =
                                RequestBody.create(
                                    MediaType.parse("image/*"),
                                    imageFile
                                )
                            multipartBody = MultipartBody.Part.createFormData(
                                "image",
                                imageFile.name,
                                requestBody
                            )
                        }
                    }
                }
                updateBlogPost.execute(
                    authToken = sessionManager.state.value?.authToken,
                    slug = state.blogPost.slug,
                    title = title,
                    body = body,
                    image = multipartBody,
                ).onEach { dataState ->
                    this.state.value = state.copy(isLoading = dataState.isLoading)

                    dataState.data?.let { response ->
                        if(response.message == SUCCESS_BLOG_UPDATED){
                            onTriggerEvent(UpdateBlogEvents.OnUpdateComplete)
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

    private fun getBlog(pk: Int){
        state.value?.let { state ->
            getBlogFromCache.execute(
                pk = pk
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { blogPost ->
                    this.state.value = state.copy(blogPost = blogPost)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }
}


















