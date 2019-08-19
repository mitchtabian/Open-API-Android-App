package com.codingwithmitch.openapi.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.ui.main.account.state.AccountStateEvent.*

import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.focusable_view


class AccountFragment : BaseAccountFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupActionBar()

        change_password.setOnClickListener{
            findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
        }

        logout_button.setOnClickListener {
            viewModel.logout()
        }

        subscribeObservers()
        focusable_view.requestFocus() // reset focus
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            stateChangeListener.onDataStateChange(dataState)
            if(dataState != null){
                dataState.data?.let {
                    it.data?.let{
                        it.getContentIfNotHandled()?.let{
                            it.accountProperties?.let{
                                Log.d(TAG, "AccountFragment, DataState: ${it}")
                                viewModel.setAccountPropertiesData(it)
                            }
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer{ viewState->
            if(viewState != null){
                viewState.accountProperties?.let{
                    Log.d(TAG, "AccountFragment, ViewState: ${it}")
                    setAccountDataFields(it)
                }
            }
        })
    }

    private fun setupActionBar(){
        activity?.actionBar?.title = getString(R.string.fragment_account)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: called")
        viewModel.setStateEvent(GetAccountPropertiesEvent())
    }

    private fun setAccountDataFields(accountProperties: AccountProperties){
        email?.setText(accountProperties.email)
        username?.setText(accountProperties.username)
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

























