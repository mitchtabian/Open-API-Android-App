package com.codingwithmitch.openapi.ui.main.blog

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.ui.main.blog.BlogRecyclerAdapter.BlogViewHolder.*
import com.codingwithmitch.openapi.util.DateUtils
import kotlinx.android.synthetic.main.layout_blog_list_item.view.*
import com.bumptech.glide.util.ViewPreloadSizeProvider
import android.text.TextUtils
import java.util.*
import kotlin.collections.ArrayList


class BlogRecyclerAdapter(
    val requestManager: RequestManager,
    val preloadSizeProvider: ViewPreloadSizeProvider<String>,
    var blogClickListener: BlogClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
        ListPreloader.PreloadModelProvider<String>
{

    private val TAG: String = "AppDebug"

    private val NO_MORE_RESULTS = -1
    private val BLOG_ITEM = 0
    private val NO_MORE_RESULTS_BLOG_MARKER = BlogPost(NO_MORE_RESULTS, "" ,"", "", "", 0, "")

    private var items: List<BlogPost> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when(viewType){

            NO_MORE_RESULTS ->{
                Log.e(TAG, "onCreateViewHolder: No more results...")
                return GenericViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.layout_no_more_results, parent, false)
                )
            }

            BLOG_ITEM ->{
                return BlogViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.layout_blog_list_item, parent, false),
                    blogClickListener = blogClickListener,
                    preloadSizeProvider = preloadSizeProvider,
                    requestManager = requestManager
                )
            }
            else -> return BlogViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.layout_blog_list_item, parent, false),
                blogClickListener = blogClickListener,
                preloadSizeProvider = preloadSizeProvider,
                requestManager = requestManager
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {

            is BlogViewHolder -> {
                holder.bind(items.get(position))
            }

            is GenericViewHolder -> {
                // do nothing
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(items.get(position).pk > -1){
            return BLOG_ITEM
        }
        return items.get(position).pk
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setNoMoreResults(){
        val newList = ArrayList<BlogPost>()
        newList.addAll(items)
        newList.add(NO_MORE_RESULTS_BLOG_MARKER)
        submitList(newList)
    }

    fun findBlogPost(position: Int): BlogPost{
        return items[position]
    }

    fun submitList(blogList: List<BlogPost>){
        val oldList = items
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(BlogItemDiffCallback(oldList, blogList))
        items = blogList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getPreloadItems(position: Int): List<String> {
        val url = items.get(position).image
        return if (TextUtils.isEmpty(url)) {
            Collections.emptyList()
        } else Collections.singletonList(url)
    }

    override fun getPreloadRequestBuilder(item: String): RequestBuilder<*>? {
        return requestManager.load(item)
    }

    class BlogViewHolder
    constructor(
        itemView: View,
        val requestManager: RequestManager,
        val preloadSizeProvider: ViewPreloadSizeProvider<String>,
        val blogClickListener: BlogClickListener
    ): RecyclerView.ViewHolder(itemView), View.OnClickListener{

        val blog_image = itemView.blog_image
        val blog_title = itemView.blog_title
        val blog_author = itemView.blog_author
        val blog_date_updated = itemView.blog_update_date

        fun bind(blogPost: BlogPost){
            requestManager
                .load(blogPost.image)
                .into(blog_image)
            blog_title.setText(blogPost.title)
            blog_author.setText(blogPost.username)
            blog_date_updated.setText(DateUtils.convertLongToStringDate(blogPost.date_updated))

            itemView.setOnClickListener(this)

            preloadSizeProvider.setView(itemView.findViewById(R.id.blog_image))
        }

        override fun onClick(v: View?) {
            blogClickListener.onBlogSelected(adapterPosition)
        }


        interface BlogClickListener{
            fun onBlogSelected(itemPosition: Int)
        }
    }

    class BlogItemDiffCallback(
        var oldBlogList: List<BlogPost>,
        var newBlogList: List<BlogPost>

    ): DiffUtil.Callback() {


        override fun areItemsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean {
            return (oldBlogList.get(oldItemPosition).pk
                    == newBlogList.get(newItemPosition).pk)
        }

        override fun getOldListSize(): Int {
            return oldBlogList.size
        }

        override fun getNewListSize(): Int {
            return newBlogList.size
        }

        override fun areContentsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean {
            return (oldBlogList.get(oldItemPosition)
                    == newBlogList.get(newItemPosition))
        }
    }
}
























