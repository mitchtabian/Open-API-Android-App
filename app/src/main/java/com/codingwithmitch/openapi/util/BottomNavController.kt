package com.codingwithmitch.openapi.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.main.account.ChangePasswordFragment
import com.codingwithmitch.openapi.ui.main.account.UpdateAccountFragment
import com.codingwithmitch.openapi.ui.main.blog.UpdateBlogFragment
import com.codingwithmitch.openapi.ui.main.blog.ViewBlogFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


/**
 * Class credit: Allan Veloso
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

    interface OnNavigationItemChanged {
        fun onItemChanged(itemId: Int)
    }

    interface NavGraphProvider {
        @NavigationRes
        fun getNavGraphId(itemId: Int): Int
    }

    interface OnNavigationGraphChanged{
        fun onGraphChange()
    }

    init {
        var ctx = context
        while (ctx is ContextWrapper) {
            if (ctx is Activity) {
                activity = ctx
                fragmentManager = (activity as FragmentActivity).supportFragmentManager
                break
            }
            ctx = ctx.baseContext
        }
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


    fun onNavigationItemReselected() {

        // WORKAROUND for bug
        fragmentManager.findFragmentById(containerId)!!.childFragmentManager.fragments[0]?.let{ fragment ->
            when(fragment){

                is ViewBlogFragment -> {
                    activity
                        .findNavController(containerId)
                        .navigate(R.id.action_viewBlogFragment_to_home)
                }

                is UpdateBlogFragment -> {
                    activity
                        .findNavController(containerId)
                        .navigate(R.id.action_updateBlogFragment_to_home)
                }

                is UpdateAccountFragment -> {
                    activity
                        .findNavController(containerId)
                        .navigate(R.id.action_updateAccountFragment_to_home)
                }

                is ChangePasswordFragment -> {
                    activity
                        .findNavController(containerId)
                        .navigate(R.id.action_changePasswordFragment_to_home)
                }
            }
        }

    }

    // THIS HAS A BUG... After popping the backstack it still thinks it's in the same fragment.
    // If you try to nav to another fragment within the same child stack, it causes a
    // "'action' is unknown to this NavController" error
//    fun onNavigationItemReselected(item: MenuItem) {
//        // If the user press a second time the navigation button, we pop the back stack to the root
//        activity.findNavController(containerId).popBackStack(item.itemId, false)
//    }


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

        listener?.onItemChanged(itemId)
        graphChangeListener?.onGraphChange()

        return true
    }

    fun onBackPressed() {
        val childFragmentManager = fragmentManager.findFragmentById(containerId)!!
            .childFragmentManager
        when {
            // We should always try to go back on the child fragment manager stack before going to
            // the navigation stack. It's important to use the child fragment manager instead of the
            // NavController because if the user change tabs super fast commit of the
            // supportFragmentManager may mess up with the NavController child fragment manager back
            // stack

            childFragmentManager.popBackStackImmediate() -> {
            }
            // Fragment back stack is empty so try to go back on the navigation stack
            navigationBackStack.size > 1 -> {
                // Remove last item from back stack
                navigationBackStack.removeLast()

                // Update the container with new fragment
                onNavigationItemSelected()

            }
            // If the stack has only one and it's not the navigation home we should
            // ensure that the application always leave from startDestination
            navigationBackStack.last() != appStartDestinationId -> {
                navigationBackStack.removeLast()
                navigationBackStack.add(0, appStartDestinationId)
                onNavigationItemSelected()
            }
            // Navigation stack is empty, so finish the activity
            else -> activity.finish()
        }
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
            remove(item)
            add(item)
        }
    }
}

// Convenience extension to set up the navigation
fun BottomNavigationView.setUpNavigation(bottomNavController: BottomNavController, onReselect: ((menuItem: MenuItem) -> Unit)? = null) {

    setOnNavigationItemSelectedListener {
        bottomNavController.onNavigationItemSelected(it.itemId)

    }
    setOnNavigationItemReselectedListener {
        bottomNavController.onNavigationItemReselected()
        onReselect?.invoke(it)
    }
    bottomNavController.setOnItemNavigationChanged { itemId ->
        menu.findItem(itemId).isChecked = true
    }

}


/**
 *
 * NavHostFragment = fragmentManager.findFragmentById(containerId)!!
 *  ex: NavHostFragment for each bottom navigation selection
 *
 * Children Fragments = fragmentManager.findFragmentById(containerId)!!.childFragmentManager.fragments
 *  ex: BlogFragment, ViewBlogFragment, UpdateBlogFragment
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */



































