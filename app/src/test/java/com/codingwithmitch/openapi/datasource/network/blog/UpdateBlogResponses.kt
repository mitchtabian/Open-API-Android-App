package com.codingwithmitch.openapi.datasource.network.blog

import com.codingwithmitch.openapi.business.domain.models.AuthToken
import com.codingwithmitch.openapi.business.domain.models.BlogPost
import com.codingwithmitch.openapi.business.domain.util.DateUtils
import com.codingwithmitch.openapi.business.domain.util.ErrorHandling
import com.codingwithmitch.openapi.business.domain.util.SuccessHandling
import org.xml.sax.ErrorHandler

object UpdateBlogResponses {

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

    val updatedTitle = "Hey look a new title"
    val updatedBody = "I don't know I just need to make sure this is 50 characters long for testing."

    val updateSuccess = "{ \"response\": \"${SuccessHandling.SUCCESS_BLOG_UPDATED}\", \"pk\": $blogPk, \"title\": \"$updatedTitle\", \"body\": \"$updatedBody\", \"slug\": \"$slug\", \"date_updated\": \"$dateUpdated\", \"image\": \"$image\", \"username\": \"$username\" }"

    val updateFail_dontHavePermission = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_EDIT_BLOG_NEED_PERMISSION}\" }"

    val updateFail_titleLength = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_BLOG_TITLE_LENGTH}\" }"

    val updateFail_bodyLength = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_BLOG_BODY_LENGTH}\" }"

    val updateFail_imageSize = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_BLOG_IMAGE_SIZE}\" }"

    val updateFail_imageAspectRatio = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_BLOG_IMAGE_ASPECT_RATIO}\" }"

}












