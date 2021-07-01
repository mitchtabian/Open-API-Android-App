package com.codingwithmitch.openapi.util

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

	companion object {

		private const val TAG: String = "AppDebug"

		// dates from server look like this: "2019-07-23T03:28:01.406944Z"
		fun convertServerStringDateToLong(sd: String): Long {
			val stringDate = sd.removeRange(sd.indexOf("T") until sd.length)
			val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
			try {
				return sdf.parse(stringDate).time
			} catch (e: Exception) {
				throw Exception(e)
			}
		}

		fun convertLongToStringDate(longDate: Long): String {
			val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
			try {
				val date = sdf.format(Date(longDate))
				return date
			} catch (e: Exception) {
				throw Exception(e)
			}
		}
	}


}