package com.codingwithmitch.openapi.datasource.network.blog

import com.codingwithmitch.openapi.business.domain.models.AuthToken
import com.codingwithmitch.openapi.business.domain.models.BlogPost
import com.codingwithmitch.openapi.business.domain.util.DateUtils
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling.Companion.ERROR_EDIT_BLOG_NEED_PERMISSION
import com.codingwithmitch.openapi.business.domain.util.SuccessHandling

object IsAuthorOfBlogPostResponses {

    // account info
    val email = "mitch_test@gmail.com"
    val password = "password"
    val pk = 1
    val username = "mitch_test"
    val token = "de803edc9ebefa3dee77faea8f34fff3e6b217b5"

    val authToken = AuthToken(
        accountPk = pk,
        token = token,
    )

    // blog info
    val blogPk = 453
    val title = "How to create a new blog post!"
    val body = "I'm publishing a blog about how to create a new blog! Wow! Amazing!"
    val image = "https://this_is_fake.com/image.png"
    val slug = "mitch1-how-to-create-a-new-blog-post"
    val dateUpdated = "2021-07-09T16:26:23.121544Z"

    val blogPost = BlogPost(
        pk = blogPk,
        title = title,
        body = body,
        image = image,
        slug = slug,
        dateUpdated = DateUtils.convertServerStringDateToLong(dateUpdated),
        username = username
    )

    val isAuthorSuccess = "{ \"response\": \"${SuccessHandling.RESPONSE_HAS_PERMISSION_TO_EDIT}\" }"
    val isAuthorFail = "{ \"response\": \"Error\", \"error_message\": \"${ERROR_EDIT_BLOG_NEED_PERMISSION}\" }"
    val isAuthorFail_randomError = "{ \"response\": \"random error\", \"error_message\": \"Something random happened.\" }"
}














