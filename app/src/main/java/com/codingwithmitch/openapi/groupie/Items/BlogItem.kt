package com.codingwithmitch.openapi.groupie.Items

import com.bumptech.glide.Glide
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.util.DateUtils
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.layout_blog_list_item.*

open class BlogItem(
    private val blogPost: BlogPost
): Item() {


    override fun getLayout(): Int {
        return R.layout.layout_blog_list_item
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        Glide.with(viewHolder.itemView)
            .load(blogPost.image)
            .into(viewHolder.blog_image)

        viewHolder.blog_title.setText(blogPost.title)
        viewHolder.blog_author.setText(blogPost.username)
        viewHolder.blog_update_date.setText(DateUtils.convertLongToStringDate(blogPost.date_updated))
    }
}