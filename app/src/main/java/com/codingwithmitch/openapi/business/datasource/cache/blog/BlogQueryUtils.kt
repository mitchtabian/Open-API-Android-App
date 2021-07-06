package com.codingwithmitch.openapi.business.datasource.cache.blog


class BlogQueryUtils {


    companion object{
        private val TAG: String = "AppDebug"

        // values
        const val BLOG_ORDER_ASC: String = ""
        const val BLOG_ORDER_DESC: String = "-"
        const val BLOG_FILTER_USERNAME = "username"
        const val BLOG_FILTER_DATE_UPDATED = "date_updated"

        val ORDER_BY_ASC_DATE_UPDATED = BLOG_ORDER_ASC + BLOG_FILTER_DATE_UPDATED
        val ORDER_BY_DESC_DATE_UPDATED = BLOG_ORDER_DESC + BLOG_FILTER_DATE_UPDATED
        val ORDER_BY_ASC_USERNAME = BLOG_ORDER_ASC + BLOG_FILTER_USERNAME
        val ORDER_BY_DESC_USERNAME = BLOG_ORDER_DESC + BLOG_FILTER_USERNAME
    }
}


