package com.templateapp.cloudapi.business.domain.util

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object{

        private val TAG: String = "AppDebug"

        // dates from server look like this: "2019-07-23T03:28:01.406944Z"
        // Convert seconds as well - for purposes of sorting. Date is not enough.
        fun convertServerStringDateToLong(sd: String): Long{
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
            try {
                val time = sdf.parse(sd).time
                return time
            } catch (e: Exception) {
                throw Exception(e)
            }
        }

        fun convertLongToStringDate(longDate: Long): String{
            // When converting back, there is no need to convert time. Date is enough.
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