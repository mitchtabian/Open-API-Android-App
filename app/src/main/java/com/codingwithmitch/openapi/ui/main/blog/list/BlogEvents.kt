package com.codingwithmitch.openapi.ui.main.blog.list

import com.codingwithmitch.openapi.util.StateMessage


sealed class BlogEvents {

    object NewSearch : BlogEvents()

    object NextPage: BlogEvents()

    data class UpdateQuery(val query: String): BlogEvents()

    data class UpdateFilter(val filter: BlogFilterOptions): BlogEvents()

    data class UpdateOrder(val order: BlogOrderOptions): BlogEvents()

    data class Error(val stateMessage: StateMessage): BlogEvents()
}
