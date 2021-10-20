package com.templateapp.cloudapi.datasource.network.blog

import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.domain.models.BlogPost
import com.templateapp.cloudapi.business.domain.util.DateUtils
import com.templateapp.cloudapi.business.domain.util.ErrorHandling
import com.templateapp.cloudapi.business.domain.util.SuccessHandling

object CreateResponses {

    // account info
    val email = "mitch_test@gmail.com"
    val password = "password"
    val id = "1"
    val username = "mitch_test"
    val token = "de803edc9ebefa3dee77faea8f34fff3e6b217b5"

    val authToken = AuthToken(
        accountId = id,
        token = token,
    )

    // blog info
    val blogId = 453
    val title = "How to create a new blog post!"
    val body = "I'm publishing a blog about how to create a new blog! Wow! Amazing!"
    val image = "https://this_is_fake.com/image.png"
    val slug = "mitch1-how-to-create-a-new-blog-post"
    val dateUpdated = "2021-07-09T16:26:23.121544Z"

    val blogPost = BlogPost(
        id = blogId,
        title = title,
        body = body,
        image = image,
        slug = slug,
        dateUpdated = DateUtils.convertServerStringDateToLong(dateUpdated),
        username = username
    )

    val createSuccess = "{ \"response\": \"${SuccessHandling.SUCCESS_BLOG_CREATED}\", \"id\": ${blogId}, \"title\": \"${title}\", \"body\": \"${body}\", \"slug\": \"${slug}\", \"date_updated\": \"${dateUpdated}\", \"image\": \"${image}\", \"username\": \"${username}\" }"

    val createFail_titleMustBe5Chars = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_BLOG_TITLE_LENGTH}\" }"

    val createFail_bodyMustBe50Chars = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_BLOG_BODY_LENGTH}\" }"

    val createFail_imageTooLarge = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_BLOG_IMAGE_SIZE}\" }"

    val createFail_imageAspectRatio = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_BLOG_IMAGE_ASPECT_RATIO}\" }"

    // If you are not a member you can't interact with the api
    val createFail_notCwmMember = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_NOT_CWM_MEMBER}\" }"


}














