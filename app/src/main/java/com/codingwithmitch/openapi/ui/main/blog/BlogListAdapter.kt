package com.codingwithmitch.openapi.ui.main.blog

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.util.DateUtils
import com.codingwithmitch.openapi.util.GenericViewHolder
import kotlinx.android.synthetic.main.layout_blog_list_item.view.*

class BlogListAdapter(
	private val requestManager: RequestManager,
	private val interaction: Interaction? = null
) :
	RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	companion object {
		private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BlogPost>() {

			override fun areItemsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
				return oldItem.pk == newItem.pk
			}

			override fun areContentsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
				return oldItem == newItem
			}

		}
		private const val TAG: String = "AppDebug"
		private const val NO_MORE_RESULTS = -1
		private const val BLOG_ITEM = 0
		private val NO_MORE_RESULTS_BLOG_MARKER = BlogPost(
			NO_MORE_RESULTS,
			"",
			"",
			"",
			"",
			0,
			""
		)
	}

	private val differ =
		AsyncListDiffer(
			BlogRecyclerChangeCallback(this),
			AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
		)


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

		when (viewType) {

			NO_MORE_RESULTS -> {
				Log.e(TAG, "onCreateViewHolder: No more results...")
				return GenericViewHolder(
					LayoutInflater.from(parent.context).inflate(
						R.layout.layout_no_more_results,
						parent,
						false
					)
				)
			}

			BLOG_ITEM -> {
				return BlogViewHolder(
					LayoutInflater.from(parent.context).inflate(
						R.layout.layout_blog_list_item,
						parent,
						false
					),
					interaction = interaction,
					requestManager = requestManager
				)
			}
			else -> {
				return BlogViewHolder(
					LayoutInflater.from(parent.context).inflate(
						R.layout.layout_blog_list_item,
						parent,
						false
					),
					interaction = interaction,
					requestManager = requestManager
				)
			}
		}
	}

	internal inner class BlogRecyclerChangeCallback(
		private val adapter: BlogListAdapter
	) : ListUpdateCallback {

		override fun onChanged(position: Int, count: Int, payload: Any?) {
			adapter.notifyItemRangeChanged(position, count, payload)
		}

		override fun onInserted(position: Int, count: Int) {
			adapter.notifyItemRangeChanged(position, count)
		}

		override fun onMoved(fromPosition: Int, toPosition: Int) {
			adapter.notifyDataSetChanged()
		}

		override fun onRemoved(position: Int, count: Int) {
			adapter.notifyDataSetChanged()
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		when (holder) {
			is BlogViewHolder -> {
				holder.bind(differ.currentList[position])
			}
		}
	}

	override fun getItemViewType(position: Int): Int {
		if (differ.currentList[position].pk > -1) {
			return BLOG_ITEM
		}
		return differ.currentList[position].pk
	}

	override fun getItemCount(): Int {
		return differ.currentList.size
	}

	// Prepare the images that will be displayed in the RecyclerView.
	// This also ensures if the network connection is lost, they will be in the cache
	fun preloadGlideImages(
		requestManager: RequestManager,
		list: List<BlogPost>
	) {
		for (blogPost in list) {
			requestManager
				.load(blogPost.image)
				.preload()
		}
	}

	fun submitList(
		blogList: List<BlogPost>?,
		isQueryExhausted: Boolean
	) {
		val newList = blogList?.toMutableList()
		if (isQueryExhausted)
			newList?.add(NO_MORE_RESULTS_BLOG_MARKER)
		val commitCallback = Runnable {
			// if process died must restore list position
			// very annoying
			interaction?.restoreListPosition()
		}
		differ.submitList(newList, commitCallback)
	}

	class BlogViewHolder
	constructor(
		itemView: View,
		val requestManager: RequestManager,
		private val interaction: Interaction?
	) : RecyclerView.ViewHolder(itemView) {

		fun bind(item: BlogPost) = with(itemView) {
			itemView.setOnClickListener {
				interaction?.onItemSelected(absoluteAdapterPosition, item)
			}

			requestManager
				.load(item.image)
				.transition(withCrossFade())
				.into(itemView.blog_image)
			itemView.blog_title.text = item.title
			itemView.blog_author.text = item.username
			itemView.blog_update_date.text = DateUtils.convertLongToStringDate(item.date_updated)
		}
	}

	interface Interaction {

		fun onItemSelected(position: Int, item: BlogPost)

		fun restoreListPosition()
	}
}
