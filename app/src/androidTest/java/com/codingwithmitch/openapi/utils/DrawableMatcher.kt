package com.codingwithmitch.openapi.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher


const val DRAWABLE_EMPTY = -1
const val DRAWABLE_ANY = -2

class DrawableMatcher(
    private val expectedId: Int,
    private var resourceName: String? = null
) : TypeSafeMatcher<View>(){

    override fun describeTo(description: Description?) {
        description?.let {desc ->
            desc.appendText("with drawable from resource id: ")
            desc.appendValue(expectedId)
            if(resourceName != null){
                desc.appendText("[")
                desc.appendText(resourceName)
                desc.appendText("]")
            }
        }
    }

    override fun matchesSafely(target: View?): Boolean {
        if (!(target is ImageView)){
            return false
        }

        val imageView: ImageView = target
        if(expectedId == DRAWABLE_EMPTY){
            return imageView.drawable == null
        }

        if(expectedId == DRAWABLE_ANY){
            return imageView.drawable != null
        }

        val resources = target.context.resources
        val expectedDrawable = ResourcesCompat.getDrawable(resources, expectedId, null)
        resourceName = resources.getResourceEntryName(expectedId)
        if(expectedDrawable == null){
            return false
        }

        val bitmap = getBitmap(imageView.drawable)
        val expectedBitmap = getBitmap(expectedDrawable)
        return bitmap.sameAs(expectedBitmap)
    }

    fun getBitmap(drawable: Drawable): Bitmap{
        val bm = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
         )
        val canvas = Canvas(bm)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bm
    }

}


















