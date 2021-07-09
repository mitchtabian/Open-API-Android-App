package com.codingwithmitch.openapi.datasource.network.blog

import com.codingwithmitch.openapi.business.domain.util.SuccessHandling

object CreateResponses {

    // account info
    val email = "mitch_test@gmail.com"
    val password = "password"
    val pk = 1
    val username = "mitch_test"
    val token = "de803edc9ebefa3dee77faea8f34fff3e6b217b5"

    // blog info
    val blogPk = 453
    val title = "How to create a new blog post!"
    val body = "I'm publishing a blog about how to create a new blog! Wow! Amazing!"
    val image = "https://this_is_fake.com/image.png"
    val slug = "mitch1-how-to-create-a-new-blog-post"
    val dateUpdated = "2021-07-09T16:26:23.121544Z"

    val createSuccess = "{ \"response\": \"${SuccessHandling.SUCCESS_BLOG_CREATED}\", \"pk\": ${blogPk}, \"title\": \"${title}\", \"body\": \"${body}\", \"slug\": \"${slug}\", \"date_updated\": \"${dateUpdated}\", \"image\": \"${image}\", \"username\": \"${username}\" }"
}














