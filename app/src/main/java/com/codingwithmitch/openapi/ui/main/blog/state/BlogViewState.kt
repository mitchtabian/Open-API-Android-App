package com.codingwithmitch.openapi.ui.main.blog.state

import com.codingwithmitch.openapi.models.BlogPost

data class BlogViewState (

    // BlogFragment vars
    var blogFields: BlogFields = BlogFields()


)
{
    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList<BlogPost>(),
        var searchQuery: String = ""
    )


}