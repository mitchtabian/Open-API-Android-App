package com.codingwithmitch.openapi.presentation.main.account.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.business.domain.models.Account
import com.codingwithmitch.openapi.business.domain.util.StateMessageCallback
import com.codingwithmitch.openapi.presentation.main.account.BaseAccountFragment
import com.codingwithmitch.openapi.presentation.util.processQueue
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : BaseAccountFragment(R.layout.fragment_account,) {

    private val viewModel: AccountViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        change_password.setOnClickListener{
            findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
        }

        logout_button.setOnClickListener {
            viewModel.onTriggerEvent(AccountEvents.Logout)
        }

        subscribeObservers()
        viewModel.onTriggerEvent(AccountEvents.GetAccount)
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(AccountEvents.OnRemoveHeadFromQueue)
                    }
                })

            state.account?.let { account ->
                setAccountDataFields(account)
            }
        })
    }

    private fun setAccountDataFields(account: Account){
        email.text = account.email
        username.text = account.username
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_view_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.edit -> {
                viewModel.state.value?.let { state ->
                    state.account?.let { account ->
                        val bundle = bundleOf("accountPk" to account.pk)
                        findNavController().navigate(R.id.action_accountFragment_to_updateAccountFragment, bundle)
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}