package com.codingwithmitch.openapi.ui.main.account


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.ui.main.account.state.AccountStateEvent.*
import kotlinx.android.synthetic.main.fragment_update_account.*


class UpdateAccountFragment : BaseAccountFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObervers()
    }

    private fun subscribeObervers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            stateChangeListener.onDataStateChange(dataState)
            Log.d(TAG, "UpdateAccountFragment, DataState: ${dataState}")
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer{ viewState ->
            if(viewState != null){
                viewState.accountProperties?.let{
                    Log.d(TAG, "UpdateAccountFragment, ViewState: ${it}")
                    setAccountDataFields(it)
                }
            }
        })
    }

    private fun setAccountDataFields(accountProperties: AccountProperties){
        if(input_email.text.isNullOrBlank()){
            input_email.setText(accountProperties.email)
        }
        if(input_username.text.isNullOrBlank()){
            input_username.setText(accountProperties.username)
        }
    }

    private fun saveChanges(){
        viewModel.setStateEvent(
            UpdateAccountPropertiesEvent(input_email.text.toString(), input_username.text.toString())
        )
        stateChangeListener.hideSoftKeyboard()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.account_update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save_account -> {
                saveChanges()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}























