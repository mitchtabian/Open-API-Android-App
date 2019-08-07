package com.codingwithmitch.openapi.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.codingwithmitch.openapi.R
import com.afollestad.materialdialogs.MaterialDialog
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.auth.AuthActivity
import com.codingwithmitch.openapi.ui.main.account.AccountStateChangeListener
import com.codingwithmitch.openapi.ui.main.account.state.AccountDataState
import com.codingwithmitch.openapi.util.BottomNavController
import com.codingwithmitch.openapi.util.setUpNavigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(),
    BottomNavController.NavGraphProvider,
    AccountStateChangeListener
{
    private val TAG: String = "AppDebug"

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var progressBar: ProgressBar
    private lateinit var bottomNavigationView: BottomNavigationView

    private val bottomNavController by lazy(LazyThreadSafetyMode.NONE) {
        BottomNavController(this, R.id.main_nav_host_fragment, R.id.nav_blog)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        progressBar = findViewById(R.id.progress_bar)

        Log.d(TAG, "bottom nav address: ${bottomNavController}")

        bottomNavController.setNavGraphProvider(this)
        bottomNavigationView.setUpNavigation(bottomNavController)

        if (savedInstanceState == null) {
            setupActionBar()
            bottomNavController.onNavigationItemSelected()
        }
        subscribeObservers()
    }

    private fun setupActionBar(){
        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        setSupportActionBar(toolbar)
    }

    override fun getNavGraphId(itemId: Int) = when (itemId) {
        R.id.nav_blog -> R.navigation.nav_blog
        R.id.nav_create_blog -> R.navigation.nav_create_blog
        R.id.nav_account -> R.navigation.nav_account
        else -> R.navigation.nav_blog
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

                when(it.loading){
                    true -> displayProgressBar(true)
                    false -> displayProgressBar(false)
                    else -> displayProgressBar(false)
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

    fun displayProgressBar(bool: Boolean){
        if(bool){
            progressBar.visibility = View.VISIBLE
        }
        else{
            progressBar.visibility = View.GONE
        }
    }


    fun displayErrorDialog(errorMessage: String){
        MaterialDialog(this)
            .title(R.string.text_error)
            .message(text = errorMessage){
                lineSpacing(2F)
            }
            .positiveButton(R.string.text_ok)
            .show()
    }

    fun displaySuccessDialog(message: String){
        MaterialDialog(this)
            .title(R.string.text_success)
            .message(text = message){
                lineSpacing(2F)
            }
            .positiveButton(R.string.text_ok)
            .show()
    }


    // Update UI when doing things in AccountFragment
    override fun onAccountDataStateChange(accountDataState: AccountDataState) {
        accountDataState.error?.let {
            displayErrorDialog(it.errorMessage)
            displayProgressBar(false)
        }
        accountDataState.loading?.let {
            displayProgressBar(true)
        }
        accountDataState.successResponse?.let {
            Log.d(TAG, "MainActivity: successResponse: ${it.message}")
            if(it.useDialog){
                displaySuccessDialog(it.message)
            }
            else{
                displayToast(it.message)
            }
            displayProgressBar(false)
        }
        accountDataState.accountProperties?.let {
            displayProgressBar(false)
        }

    }


    private fun displayToast(message: String?){
        Toast.makeText(this, message, LENGTH_SHORT).show()
    }



    override fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }


//    fun showSoftKeyboard(view: View) {
//        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        view.requestFocus()
//        inputMethodManager.showSoftInput(view, 0)
//    }
}






















