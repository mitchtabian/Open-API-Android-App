package com.templateapp.cloudapi.business.datasource.cache.task


class TaskQueryUtils {


    companion object{
        private val TAG: String = "AppDebug"

        // values
        const val TASK_ORDER_ASC: String = ":asc"
        const val TASK_ORDER_DESC: String = ":desc"
        const val TASK_FILTER_USERNAME = "username"
        const val TASK_FILTER_DATE_CREATED = "createdAt"
        const val TASK_FILTER_DATE_UPDATED = "updatedAt"

        val ORDER_BY_ASC_DATE_CREATED = TASK_FILTER_DATE_CREATED + TASK_ORDER_ASC
        val ORDER_BY_DESC_DATE_CREATED = TASK_FILTER_DATE_CREATED + TASK_ORDER_DESC
        val ORDER_BY_ASC_DATE_UPDATED = TASK_FILTER_DATE_UPDATED + TASK_ORDER_ASC
        val ORDER_BY_DESC_DATE_UPDATED = TASK_FILTER_DATE_UPDATED + TASK_ORDER_DESC
        val ORDER_BY_ASC_USERNAME =  TASK_FILTER_USERNAME + TASK_ORDER_ASC
        val ORDER_BY_DESC_USERNAME = TASK_FILTER_USERNAME + TASK_ORDER_DESC
    }
}


