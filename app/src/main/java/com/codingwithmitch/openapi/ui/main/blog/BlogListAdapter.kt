package com.codingwithmitch.openapi.ui.main.blog

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.util.DateUtils
import kotlinx.android.synthetic.main.layout_blog_list_item.view.*
import com.codingwithmitch.openapi.util.GenericViewHolder
import androidx.recyclerview.widget.AsyncListDiffer




class BlogListAdapter(
    val requestManager: RequestManager,
    var blogClickListener: BlogViewHolder.BlogClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    private val TAG: String = "AppDebug"

    private val NO_MORE_RESULTS = -1
    private val BLOG_ITEM = 0
    private val NO_MORE_RESULTS_BLOG_MARKER = BlogPost(NO_MORE_RESULTS, "" ,"", "", "", 0, "")

    val DIFF_CALLBACK = object: DiffUtil.ItemCallback<BlogPost>(){

        override fun areItemsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
            return oldItem.pk == newItem.pk
        }

        override fun areContentsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when(viewType){

            NO_MORE_RESULTS ->{
                Log.e(TAG, "onCreateViewHolder: No more results...")
                return GenericViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_no_more_results,
                        parent,
                        false
                    )
                )
            }

            BLOG_ITEM ->{
                return BlogViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.layout_blog_list_item, parent, false),
                    blogClickListener = blogClickListener,
                    requestManager = requestManager
                )
            }
            else -> return BlogViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.layout_blog_list_item, parent, false),
                blogClickListener = blogClickListener,
                requestManager = requestManager
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {

            is BlogViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }

            is GenericViewHolder -> {
                // do nothing
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(differ.currentList.get(position).pk > -1){
            return BLOG_ITEM
        }
        return differ.currentList.get(position).pk
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    fun submitList(blogList: List<BlogPost>, isQueryExhausted: Boolean){
        val newList = blogList.toMutableList()
        if (isQueryExhausted)
            newList.add(NO_MORE_RESULTS_BLOG_MARKER)
        differ.submitList(newList)
    }

    fun findBlogPost(position: Int): BlogPost{
        return differ.currentList[position]
    }


    class BlogViewHolder
    constructor(
        itemView: View,
        val requestManager: RequestManager,
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
        }

        override fun onClick(v: View?) {
            blogClickListener.onBlogSelected(adapterPosition)
        }


        interface BlogClickListener{
            fun onBlogSelected(itemPosition: Int)
        }
    }

}
























