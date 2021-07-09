package com.codingwithmitch.openapi.datasource.network.blog

import com.codingwithmitch.openapi.business.domain.models.AuthToken
import com.codingwithmitch.openapi.business.domain.models.BlogPost
import com.codingwithmitch.openapi.business.domain.util.DateUtils

object ConfirmBlogExistsOnServerResponses {

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
        dateUpdated = DateUtils.convertServerStringDateToLong(CreateResponses.dateUpdated),
        username = username
    )

    val success_blogExists = "{ \"pk\": $pk, \"title\": \"$title\", \"slug\": \"$slug\", \"body\": \"$body\", \"image\": \"$image\", \"date_updated\": \"$dateUpdated\", \"username\": \"$username\" }"

}

















