package com.codingwithmitch.openapi.ui.main.blog

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.util.DateUtils
import kotlinx.android.synthetic.main.layout_blog_list_item.view.*
import com.codingwithmitch.openapi.util.GenericViewHolder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.codingwithmitch.openapi.R


class BlogListAdapter(
    private val requestManager: RequestManager,
    private var blogClickListener: BlogViewHolder.BlogClickListener

) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    private val TAG: String = "AppDebug"

    private val NO_MORE_RESULTS = -1
    private val BLOG_ITEM = 0
    private val NO_MORE_RESULTS_BLOG_MARKER = BlogPost(
        NO_MORE_RESULTS,
        "" ,
        "",
        "",
        "",
        0,
        ""
    )

    val DIFF_CALLBACK = object: DiffUtil.ItemCallback<BlogPost>(){

        override fun areItemsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
            return oldItem.pk == newItem.pk
        }

        override fun areContentsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
            return oldItem == newItem
        }
    }
    private val differ =
        AsyncListDiffer(
            BlogRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class BlogRecyclerChangeCallback(
        private val adapter: BlogListAdapter
    ) : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            Log.d(TAG, "MyCallback: onChanged...")
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            Log.d(TAG, "MyCallback: onInserted...position,count: ${position}, $count")
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            Log.d(TAG, "MyCallback: onMoved...")
            adapter.notifyDataSetChanged()
        }

        override fun onRemoved(position: Int, count: Int) {
            Log.d(TAG, "MyCallback: onRemoved...")
            adapter.notifyDataSetChanged()
        }
    }

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
            else -> {
                return BlogViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.layout_blog_list_item, parent, false),
                    blogClickListener = blogClickListener,
                    requestManager = requestManager
                )
            }
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


    fun submitList(blogList: List<BlogPost>?, isQueryExhausted: Boolean){
        val newList = blogList?.toMutableList()
        if (isQueryExhausted)
            newList?.add(NO_MORE_RESULTS_BLOG_MARKER)
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


        init {
            itemView.setOnClickListener(this)
        }

        fun bind(blogPost: BlogPost) = with(itemView){
            requestManager
                .load(blogPost.image)
                .transition(withCrossFade())
                .into(itemView.blog_image)
            itemView.blog_title.text = blogPost.title
            itemView.blog_author.text = blogPost.username
            itemView.blog_update_date.text = DateUtils.convertLongToStringDate(blogPost.date_updated)
        }

        override fun onClick(v: View?) {
            blogClickListener.onBlogSelected(adapterPosition)
        }


        interface BlogClickListener{
            fun onBlogSelected(itemPosition: Int)
        }
    }

}
























