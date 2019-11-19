package com.codingwithmitch.openapi.utils

import android.view.View
import org.hamcrest.Matcher

class EspressoTestMatchers{

    companion object{

        @JvmStatic
        fun withDrawable(resourceId: Int): Matcher<View> {
            return DrawableMatcher(resourceId)
        }

        @JvmStatic
        fun noDrawable(): Matcher<View>{
            return DrawableMatcher(DRAWABLE_EMPTY)
        }

        @JvmStatic
        fun hasDrawable(): Matcher<View>{
            return DrawableMatcher(DRAWABLE_ANY)
        }

    }


}