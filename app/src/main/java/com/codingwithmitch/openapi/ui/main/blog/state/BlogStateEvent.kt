package com.codingwithmitch.openapi.ui.main.blog.state

import com.codingwithmitch.openapi.repository.main.BlogQueryUtils


sealed class BlogStateEvent{

    data class BlogSearchEvent(
        val searchQuery: String,
        val order: String = BlogQueryUtils.ORDER_BY_ASC_DATE_UPDATED,
        val page: Int = 1
        ): BlogStateEvent()

    class NextPageEvent: BlogStateEvent()

    class None: BlogStateEvent()


}