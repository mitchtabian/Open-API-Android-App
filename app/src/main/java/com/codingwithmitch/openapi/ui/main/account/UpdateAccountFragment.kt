package com.codingwithmitch.openapi.ui.main.account


import android.os.Bundle
import android.view.*

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.ui.main.account.state.AccountDataState
import kotlinx.android.synthetic.main.fragment_update_account.*


class UpdateAccountFragment : AccountBaseFragment() {

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
    }

    override fun observeDataFromChildFragment(accountDataState: AccountDataState) {
        accountDataState.accountProperties?.let {
            setAccountDataFields(it)
        }
    }

    private fun setAccountDataFields(accountProperties: AccountProperties){
        input_email.setText(accountProperties.email)
        input_username.setText(accountProperties.username)
    }

    private fun saveChanges(){
        viewModel.saveAccountProperties(input_email.text.toString(), input_username.text.toString())
        accountStateChangeListener.hideSoftKeyboard()
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























