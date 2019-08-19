package com.codingwithmitch.openapi.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.*
import com.codingwithmitch.openapi.ui.auth.AuthActivity
import com.codingwithmitch.openapi.ui.main.account.*
import com.codingwithmitch.openapi.ui.main.blog.BaseBlogFragment
import com.codingwithmitch.openapi.ui.main.create_blog.BaseCreateFragment
import com.codingwithmitch.openapi.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(),
    BottomNavController.NavGraphProvider,
    BottomNavController.OnNavigationGraphChanged
{

    private val TAG: String = "AppDebug"

    private lateinit var bottomNavigationView: BottomNavigationView

    private val bottomNavController by lazy(LazyThreadSafetyMode.NONE) {
        BottomNavController(this, R.id.main_nav_host_fragment, R.id.nav_blog)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        setupActionBar()

        bottomNavController.setNavGraphProvider(this)
        bottomNavController.setNavGraphChangeListener(this)
        bottomNavigationView.setUpNavigation(bottomNavController)
        if (savedInstanceState == null) {
            bottomNavController.onNavigationItemSelected()
        }
        subscribeObservers()
    }

    private fun setupActionBar(){
        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        setSupportActionBar(toolbar)
    }

    override fun getNavGraphId(itemId: Int) = when (itemId) {
        R.id.nav_blog -> {
            R.navigation.nav_blog
        }
        R.id.nav_create_blog -> {
            R.navigation.nav_create_blog
        }
        R.id.nav_account -> {
            R.navigation.nav_account
        }
        else -> {
            R.navigation.nav_blog
        }
    }

    // Cancel previous jobs when navigating to a new graph
    override fun onGraphChange() {
        cancelActiveJobs()
    }

    private fun cancelActiveJobs(){
        val fragments = bottomNavController.fragmentManager.findFragmentById(bottomNavController.containerId)?.childFragmentManager?.fragments
        if(fragments != null){
            for(fragment in fragments){
                if(fragment is BaseAccountFragment){
                    fragment.cancelPreviousJobs()
                }
                if(fragment is BaseBlogFragment){
                    fragment.cancelPreviousJobs()
                }
                if(fragment is BaseCreateFragment){
                    fragment.cancelPreviousJobs()
                }
            }
        }
    }

    override fun onBackPressed() = bottomNavController.onBackPressed()

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    // This was shown to cause problems in testing due to our custom navigation system with the nav bar
    // Instead I'll be using 'onOptionsItemSelected'
//    override fun onSupportNavigateUp(): Boolean = navController
//        .navigateUp()

    fun subscribeObservers(){
        sessionManager.observeSession().observe(this, Observer {
            it?.let {
                if(it.authToken?.account_pk == -1 || it.authToken?.token == null){
                    navAuthActivity()
                    finish()
                }

                it.errorMessage?.let{
                    displayErrorDialog(it)
                }
            }
        })
    }

    private fun navAuthActivity(){
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(bool: Boolean){
        if(bool){
            progress_bar.visibility = View.VISIBLE
        }
        else{
            progress_bar.visibility = View.GONE
        }
    }

}






















