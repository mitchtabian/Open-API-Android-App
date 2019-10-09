package com.codingwithmitch.openapi.ui.main.blog.state

sealed class BlogStateEvent {

    class BlogSearchEvent : BlogStateEvent()

    class None: BlogStateEvent()
}