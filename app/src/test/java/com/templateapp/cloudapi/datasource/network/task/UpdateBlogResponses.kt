package com.templateapp.cloudapi.datasource.network.task

import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.domain.util.DateUtils
import com.templateapp.cloudapi.business.domain.util.ErrorHandling
import com.templateapp.cloudapi.business.domain.util.SuccessHandling

object UpdateBlogResponses {

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

    // task info
    val blogPk = 453
    val title = "How to create a new task!"
    val body = "I'm publishing a new task! Wow! Amazing!"
    val image = "https://this_is_fake.com/image.png"
    val slug = "mitch1-how-to-create-a-new-task"
    val dateUpdated = "2021-07-09T16:26:23.121544Z"

    val blogPost = Task(
        id = blogPk,
        title = title,
        body = body,
        image = image,
        slug = slug,
        dateUpdated = DateUtils.convertServerStringDateToLong(dateUpdated),
        username = username
    )

    val updatedTitle = "Hey look a new title"
    val updatedBody = "I don't know I just need to make sure this is 50 characters long for testing."

    val updateSuccess = "{ \"response\": \"${SuccessHandling.SUCCESS_TASK_UPDATED}\", \"id\": $blogPk, \"title\": \"$updatedTitle\", \"body\": \"$updatedBody\", \"slug\": \"$slug\", \"date_updated\": \"$dateUpdated\", \"image\": \"$image\", \"username\": \"$username\" }"

    val updateFail_dontHavePermission = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_EDIT_TASK_NEED_PERMISSION}\" }"

    val updateFail_titleLength = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_TASK_TITLE_LENGTH}\" }"

    val updateFail_bodyLength = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_TASK_BODY_LENGTH}\" }"

    val updateFail_imageSize = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_TASK_IMAGE_SIZE}\" }"

    val updateFail_imageAspectRatio = "{ \"response\": \"Error\", \"error_message\": \"${ErrorHandling.ERROR_TASK_IMAGE_ASPECT_RATIO}\" }"

}












