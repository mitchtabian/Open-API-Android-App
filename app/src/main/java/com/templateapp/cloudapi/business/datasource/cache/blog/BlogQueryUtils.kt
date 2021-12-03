package com.templateapp.cloudapi.business.datasource.cache.blog


class BlogQueryUtils {


    companion object{
        private val TAG: String = "AppDebug"

        // values
        const val BLOG_ORDER_ASC: String = ":asc"
        const val BLOG_ORDER_DESC: String = ":desc"
        const val BLOG_FILTER_USERNAME = "username"
        const val BLOG_FILTER_DATE_CREATED = "createdAt"
        const val BLOG_FILTER_DATE_UPDATED = "updatedAt"

        val ORDER_BY_ASC_DATE_CREATED = BLOG_FILTER_DATE_CREATED + BLOG_ORDER_ASC
        val ORDER_BY_DESC_DATE_CREATED = BLOG_FILTER_DATE_CREATED + BLOG_ORDER_DESC
        val ORDER_BY_ASC_DATE_UPDATED = BLOG_FILTER_DATE_UPDATED + BLOG_ORDER_ASC
        val ORDER_BY_DESC_DATE_UPDATED = BLOG_FILTER_DATE_UPDATED + BLOG_ORDER_DESC
        val ORDER_BY_ASC_USERNAME =  BLOG_FILTER_USERNAME + BLOG_ORDER_ASC
        val ORDER_BY_DESC_USERNAME = BLOG_FILTER_USERNAME + BLOG_ORDER_DESC
    }
}


