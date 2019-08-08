package com.codingwithmitch.openapi.ui.main.blog.state

import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.ui.main.account.state.AccountDataState
import com.codingwithmitch.openapi.util.Loading
import com.codingwithmitch.openapi.util.StateError
import com.codingwithmitch.openapi.util.SuccessResponse
import java.lang.Exception
import kotlin.reflect.KClass

data class BlogDataState(
    var error: StateError? = null,
    var loading: Loading? = null,
    var success: SuccessResponse? = null,
    var blogPost: BlogPost? = null,
    var blogPostList: List<BlogPost>? = null
){

    companion object{

        fun error(errorMessage: String, useDialog: Boolean): BlogDataState {
            return BlogDataState(
                error = StateError(errorMessage,  useDialog),
                loading = null,
                success = null,
                blogPostList = null
            )
        }

        fun loading(
            cachedBlogPost: BlogPost? = null,
            cachedBlogPostList: List<BlogPost>? = null

        ): BlogDataState {
            return BlogDataState(
                error = null,
                loading = Loading(),
                success = null,
                blogPost = cachedBlogPost,
                blogPostList = cachedBlogPostList
            )
        }

        fun success(message: String?, useDialog: Boolean): BlogDataState {
            return BlogDataState(
                error = null,
                loading = null,
                success = SuccessResponse(message, useDialog),
                blogPost = null,
                blogPostList = null
            )

        }

        fun blogPost(blogPost: BlogPost): BlogDataState {
            return BlogDataState(
                error = null,
                loading = null,
                success = null,
                blogPost = blogPost,
                blogPostList = null
            )
        }

        fun blogPostList(blogPostList: List<BlogPost>): BlogDataState {
            return BlogDataState(
                error = null,
                loading = null,
                success = null,
                blogPost = null,
                blogPostList = blogPostList
            )
        }
    }

}














