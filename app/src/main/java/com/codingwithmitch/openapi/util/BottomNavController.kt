package com.codingwithmitch.openapi.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.codingwithmitch.openapi.R

/**
 * Class credit: Allan Veloso
 * I took the concept from Allan Veloso and made alterations to fit our needs.
 * https://stackoverflow.com/questions/50577356/android-jetpack-navigation-bottomnavigationview-with-youtube-or-instagram-like#_=_
 * @property navigationBackStack: Backstack for the bottom navigation
 */
class BottomNavController(
    val context: Context,
    @IdRes val containerId: Int,
    @IdRes val appStartDestinationId: Int
) {

    private val TAG: String = "AppDebug"
    private val navigationBackStack = BackStack.of(appStartDestinationId)
    lateinit var activity: Activity
    lateinit var fragmentManager: FragmentManager
    private var listener: OnNavigationItemChanged? = null
    private var navGraphProvider: NavGraphProvider? = null
    private var graphChangeListener: OnNavigationGraphChanged? = null

    init {
        if (context is Activity) {
            activity = context
            fragmentManager = (activity as FragmentActivity).supportFragmentManager
        }
    }

    fun onNavigationItemSelected(itemId: Int = navigationBackStack.last()): Boolean {

        // Replace fragment representing a navigation item
        val fragment = fragmentManager.findFragmentByTag(itemId.toString())
            ?: NavHostFragment.create(navGraphProvider?.getNavGraphId(itemId)
                ?: throw RuntimeException("You need to set up a NavGraphProvider with " +
                        "BottomNavController#setNavGraphProvider")
            )
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(containerId, fragment, itemId.toString())
            .addToBackStack(null)
            .commit()

        // Add to back stack
        navigationBackStack.moveLast(itemId)

        // notify UI
        listener?.onItemChanged(itemId)
        graphChangeListener?.onGraphChange()

        return true
    }

    private class BackStack : ArrayList<Int>() {
        companion object {
            fun of(vararg elements: Int): BackStack {
                val b = BackStack()
                b.addAll(elements.toTypedArray())
                return b
            }
        }

        fun removeLast() = removeAt(size - 1)

        fun moveLast(item: Int) {
            remove(item) // if present, remove
            add(item) // add to end of list
        }
    }


    interface OnNavigationItemChanged {
        fun onItemChanged(itemId: Int)
    }

    // Get id of each graph
    // ex: R.navigation.nav_blog
    // ex: R.navigation.nav_create_blog
    interface NavGraphProvider {
        @NavigationRes
        fun getNavGraphId(itemId: Int): Int
    }

    // Execute when Navigation Graph changes.
    // ex: Select a new item on the bottom navigation
    // ex: Home -> Account
    interface OnNavigationGraphChanged{
        fun onGraphChange()
    }

    interface OnNavigationReselectedListener{

        fun onReselectNavItem(navController: NavController, fragment: Fragment)
    }

    fun setOnItemNavigationChanged(listener: (itemId: Int) -> Unit) {
        this.listener = object : OnNavigationItemChanged {
            override fun onItemChanged(itemId: Int) {
                listener.invoke(itemId)
            }
        }
    }

    fun setNavGraphProvider(provider: NavGraphProvider) {
        navGraphProvider = provider
    }

    fun setNavGraphChangeListener(graphChangeListener: OnNavigationGraphChanged){
        this.graphChangeListener = graphChangeListener
    }

}



















