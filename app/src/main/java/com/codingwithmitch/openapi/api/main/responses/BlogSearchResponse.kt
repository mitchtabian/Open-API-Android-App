package com.codingwithmitch.openapi.api.main.responses

import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.util.DateUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BlogSearchResponse(

	@SerializedName("pk")
	@Expose
	var pk: Int,

	@SerializedName("title")
	@Expose
	var title: String,

	@SerializedName("slug")
	@Expose
	var slug: String,

	@SerializedName("body")
	@Expose
	var body: String,

	@SerializedName("image")
	@Expose
	var image: String,

	@SerializedName("date_updated")
	@Expose
	var date_updated: String,

	@SerializedName("username")
	@Expose
	var username: String


) {
	fun toBlogPost(): BlogPost {
		return BlogPost(
			pk = pk,
			title = title,
			slug = slug,
			body = body,
			image = image,
			date_updated = DateUtils.convertServerStringDateToLong(
				date_updated
			),
			username = username
		)
	}

	override fun toString(): String {
		return "BlogSearchResponse(pk=$pk, title='$title', slug='$slug',  image='$image', date_updated='$date_updated', username='$username')"
	}
}















