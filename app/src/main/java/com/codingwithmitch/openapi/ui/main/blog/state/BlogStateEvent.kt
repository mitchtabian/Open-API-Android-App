package com.codingwithmitch.openapi.ui.main.blog.state

import com.codingwithmitch.openapi.util.StateEvent
import okhttp3.MultipartBody

sealed class BlogStateEvent : StateEvent {

	class BlogSearchEvent(
		val clearLayoutManagerState: Boolean = true
	) : BlogStateEvent() {
		override fun errorInfo(): String {
			return "Error searching for blog posts."
		}

		override fun toString(): String {
			return "BlogSearchEvent"
		}
	}

	class CheckAuthorOfBlogPost : BlogStateEvent() {
		override fun errorInfo(): String {
			return "Error checking if you are the author of this blog post."
		}

		override fun toString(): String {
			return "CheckAuthorOfBlogPost"
		}

		override fun equals(other: Any?): Boolean {
			return this === other
		}

		override fun hashCode(): Int {
			return System.identityHashCode(this)
		}

	}

	class DeleteBlogPostEvent : BlogStateEvent() {
		override fun errorInfo(): String {
			return "Error deleting that blog post."
		}

		override fun toString(): String {
			return "DeleteBlogPostEvent"
		}

		override fun equals(other: Any?): Boolean {
			return this === other
		}

		override fun hashCode(): Int {
			return System.identityHashCode(this)
		}
	}

	data class UpdateBlogPostEvent(
		val title: String,
		val body: String,
		val image: MultipartBody.Part?
	) : BlogStateEvent() {
		override fun errorInfo(): String {
			return "Error updating that blog post."
		}

		override fun toString(): String {
			return "UpdateBlogPostEvent"
		}

	}

	class None : BlogStateEvent() {
		override fun errorInfo(): String {
			return "None."
		}

		override fun equals(other: Any?): Boolean {
			return this === other
		}

		override fun hashCode(): Int {
			return System.identityHashCode(this)
		}
	}
}