package com.codingwithmitch.openapi.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import com.codingwithmitch.openapi.api.GenericApiResponse
import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.api.main.network_responses.BlogListSearchResponse
import com.codingwithmitch.openapi.api.main.network_responses.BlogSearchResponse
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistence.BlogPostDao
import com.codingwithmitch.openapi.ui.main.blog.state.BlogDataState
import com.codingwithmitch.openapi.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class BlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao
)
{
    private val TAG: String = "AppDebug"
    var repositoryCoroutineScope: CoroutineScope = CoroutineScope(IO)


    fun searchBlogPosts(authToken: AuthToken, query: String, ordering: String, page: Int): LiveData<BlogDataState> {
        return object: BlogNetworkBoundResource<BlogListSearchResponse>(){

            override fun loadFromDb(): LiveData<List<BlogPost>> {
                return BlogQueryUtils.returnOrderedBlogQuery(
                    blogPostDao = blogPostDao,
                    query = query,
                    ordering = ordering,
                    page = page
                    )
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogListSearchResponse>> {
                return openApiMainService.searchListBlogPosts(
                    "Token ${authToken.token!!}",
                    query = query,
                    ordering = ordering,
                    page = page
                )
            }

            override fun updateLocalDb(blogPostList: List<BlogSearchResponse>?) {

                // loop through list and update the local db
                blogPostList?.let {blogListResponse ->
                    repositoryCoroutineScope.launch {
                        for(blogPostResponse in blogListResponse){
                            try{
                                // Launch each insert as a separate job to be executed in parallel
                                launch {
                                    val blogPost = BlogPost(
                                        pk = blogPostResponse.pk,
                                        title = blogPostResponse.title,
                                        slug = blogPostResponse.slug,
                                        body = blogPostResponse.body,
                                        image = blogPostResponse.image,
                                        date_updated = DateUtils.convertServerStringDateToLong(blogPostResponse.date_updated),
                                        username = blogPostResponse.username
                                    )
                                    blogPostDao.insert(blogPost)
                                }
                            }catch (e: Exception){
                                Log.e(TAG, "updateLocalDb: error updating cache data on blog post with slug: ${blogPostResponse.slug}. " +
                                        "${e.message}")
                                // Could send an error report here or something but I don't think you should throw an error to the UI
                                // Since there could be many blog posts being inserted/updated.
                            }
                        }
                    }
                }
            }

            override fun getCoroutineScope(): CoroutineScope {
                return repositoryCoroutineScope
            }

        }.asLiveData()
    }


    fun cancelRequests(){
        repositoryCoroutineScope.cancel()
    }

}


























