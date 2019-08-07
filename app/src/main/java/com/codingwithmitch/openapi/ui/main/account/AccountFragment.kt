package com.codingwithmitch.openapi.ui.main.account

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.ui.auth.AuthViewModel

import com.codingwithmitch.openapi.ui.main.BaseFragment
import com.codingwithmitch.openapi.ui.main.account.state.AccountDataState
import kotlinx.android.synthetic.main.fragment_account.*


class AccountFragment : BaseFragment() {

    lateinit var viewModel: AccountViewModel

    private lateinit var accountStateChangeListener: AccountStateChangeListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // setup back navigation for this graph
        setupActionBarWithNavController(R.id.accountFragment, activity as AppCompatActivity)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this, providerFactory).get(AccountViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        change_password.setOnClickListener{
            findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
        }

        logout_button.setOnClickListener {
            viewModel.logout()
        }

        subscribeObservers()
        viewModel.getAccountProperties()
        setHasOptionsMenu(true)
    }

    private fun subscribeObservers(){
        viewModel.observeDataState().observe(this, Observer {
            // send state to activity for UI updates
            // ex: progress bar and material dialog
            // NOTE: error, loading, successResponse and accountProperties are passed to "accountStateChangeListener"
            //       and action will be taken in MainActivity
            accountStateChangeListener.onAccountDataStateChange(it)

            it.loading?.let {
                it.cachedData?.let { properties -> setAccountDataFields(properties as AccountProperties) }
            }
            it.accountProperties?.let {
                setAccountDataFields(it)
            }
        })

    }

    private fun setAccountDataFields(accountProperties: AccountProperties){
        email?.setText(accountProperties.email)
        username?.setText(accountProperties.username)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            accountStateChangeListener = context as AccountStateChangeListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement OnArticleSelectedListene" )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.account_view_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.edit_account -> {
                findNavController().navigate(R.id.action_accountFragment_to_updateAccountFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

























