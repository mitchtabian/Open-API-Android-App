package com.codingwithmitch.openapi.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistence.BlogPostDao

class BlogQueryUtils{

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

        /**
         * Options:
         * 1) -date_updated (DESC)
         * 2) date_updated (ASC)
         * 3) -username (alphabetical DESC)
         * 4) username (alphabetical ASC)
         * @see BlogQueryUtils
         *
         * I didn't want to have to build this class but it's necessary because you can't use variables as fields in
         * a Room query. (ex: can't pass 'date_updated' and use it as a field reference in the query)
         * @see BlogPostDao
         */
        fun returnOrderedBlogQuery(blogPostDao: BlogPostDao, query: String, filterAndOrder: String, page: Int): LiveData<List<BlogPost>> {

            when{

                filterAndOrder.contains(ORDER_BY_DESC_DATE_UPDATED) ->{
                    return blogPostDao.searchBlogPostsOrderByDateDESC(
                        query = query,
                        page = page)
                }

                filterAndOrder.contains(ORDER_BY_ASC_DATE_UPDATED) ->{
                    return blogPostDao.searchBlogPostsOrderByDateASC(
                        query = query,
                        page = page)
                }

                filterAndOrder.contains(ORDER_BY_DESC_USERNAME) ->{
                    return blogPostDao.searchBlogPostsOrderByAuthorDESC(
                        query = query,
                        page = page)
                }

                filterAndOrder.contains(ORDER_BY_ASC_USERNAME) ->{
                    return blogPostDao.searchBlogPostsOrderByAuthorASC(
                        query = query,
                        page = page)
                }
                else -> throw Exception("Must specify a valid order for all blog queries. See BLogQueryUtils class.")
            }
        }
    }
}























