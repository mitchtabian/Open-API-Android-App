package com.codingwithmitch.openapi.ui.main.blog.state

import com.codingwithmitch.openapi.util.StateEvent
import okhttp3.MultipartBody

sealed class BlogStateEvent: StateEvent {

    class BlogSearchEvent(
        val clearLayoutManagerState: Boolean = true
    ) : BlogStateEvent() {
        override fun errorInfo(): String {
            return "Error searching for blog posts."
        }
    }

    class CheckAuthorOfBlogPost: BlogStateEvent() {
        override fun errorInfo(): String {
            return "Error checking if you are the author of this blog post."
        }
    }

    class DeleteBlogPostEvent: BlogStateEvent() {
        override fun errorInfo(): String {
            return "Error deleting that blog post."
        }
    }

    data class UpdateBlogPostEvent(
        val title: String,
        val body: String,
        val image: MultipartBody.Part?
    ): BlogStateEvent() {
        override fun errorInfo(): String {
            return "Error updating that blog post."
        }
    }

    class IsBlogCacheEmpty(): BlogStateEvent(){
        override fun errorInfo(): String {
            return "Error determining if the cache is empty."
        }
    }

    class None: BlogStateEvent() {
        override fun errorInfo(): String {
            return "None."
        }
    }
}