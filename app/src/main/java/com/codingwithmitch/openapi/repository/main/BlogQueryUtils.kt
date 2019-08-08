package com.codingwithmitch.openapi.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistence.BlogPostDao

class BlogQueryUtils{

    companion object{

        private val TAG: String = "AppDebug"

        val ORDER_BY_ASC_DATE_UPDATED = "date_updated"
        val ORDER_BY_DESC_DATE_UPDATED = "-date_updated"
        val ORDER_BY_ASC_USERNAME = "username"
        val ORDER_BY_DESC_USERNAME = "-username"

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
        fun returnOrderedBlogQuery(blogPostDao: BlogPostDao, query: String, ordering: String, page: Int): LiveData<List<BlogPost>> {

            when{

                ordering.contains(ORDER_BY_DESC_DATE_UPDATED) ->{
                    return blogPostDao.searchBlogPostsOrderByDateDESC(
                        query = query,
                        page = page)
                }

                ordering.contains(ORDER_BY_ASC_DATE_UPDATED) ->{
                    return blogPostDao.searchBlogPostsOrderByDateASC(
                        query = query,
                        page = page)
                }

                ordering.contains(ORDER_BY_DESC_USERNAME) ->{
                    return blogPostDao.searchBlogPostsOrderByAuthorDESC(
                        query = query,
                        page = page)
                }

                ordering.contains(ORDER_BY_ASC_USERNAME) ->{
                    return blogPostDao.searchBlogPostsOrderByAuthorASC(
                        query = query,
                        page = page)
                }
                else -> throw Exception("Must specify a valid order for all blog queries. See BLogQueryUtils class.")
            }
        }
    }
}























