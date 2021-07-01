package com.codingwithmitch.openapi.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Local data class for modeling: https://open-api.xyz/ blog objects
see example: https://gist.github.com/mitchtabian/93f287bd1370e7a1ad3c9588b0b22e3d
 * Docs: https://open-api.xyz/api/
 */

@Parcelize
@Entity(tableName = "blog_post")
data class BlogPost(

	@PrimaryKey(autoGenerate = false)
	@ColumnInfo(name = "pk")
	var pk: Int,

	@ColumnInfo(name = "title")
	var title: String,

	@ColumnInfo(name = "slug")
	var slug: String,

	@ColumnInfo(name = "body")
	var body: String,

	@ColumnInfo(name = "image")
	var image: String,

	@ColumnInfo(name = "date_updated")
	var date_updated: Long,

	@ColumnInfo(name = "username")
	var username: String


) : Parcelable {

	override fun toString(): String {
		return "BlogPost(pk=$pk, " +
				"title='$title', " +
				"slug='$slug', " +
				"image='$image', " +
				"date_updated=$date_updated, " +
				"username='$username')"
	}


}
