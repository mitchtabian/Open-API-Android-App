package com.codingwithmitch.openapi.ui.main.account

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.codingwithmitch.openapi.ui.main.account.state.AccountStateEvent
import com.codingwithmitch.openapi.ui.main.account.state.AccountViewState
import com.codingwithmitch.openapi.util.StateMessageCallback
import kotlinx.android.synthetic.main.fragment_update_account.*
import kotlinx.coroutines.FlowPreview

@FlowPreview
class UpdateAccountFragment : BaseAccountFragment(R.layout.fragment_update_account) {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		// Restore state after process death
		savedInstanceState?.let { inState ->
			(inState[ACCOUNT_VIEW_STATE_BUNDLE_KEY] as AccountViewState?)?.let { viewState ->
				viewModel.setViewState(viewState)
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setHasOptionsMenu(true)
		subscribeObservers()
	}

	private fun subscribeObservers() {

		viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
			if (viewState != null) {
				viewState.accountProperties?.let {
					setAccountDataFields(it)
				}
			}
		})

		viewModel.numActiveJobs.observe(viewLifecycleOwner, {
			uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
		})

		viewModel.stateMessage.observe(viewLifecycleOwner, { stateMessage ->

			stateMessage?.let {

				uiCommunicationListener.onResponseReceived(
					response = it.response,
					stateMessageCallback = object : StateMessageCallback {
						override fun removeMessageFromStack() {
							viewModel.clearStateMessage()
						}
					}
				)
			}
		})
	}

	private fun setAccountDataFields(accountProperties: AccountProperties) {
		if (input_email.text.isNullOrBlank()) {
			input_email.setText(accountProperties.email)
		}
		if (input_username.text.isNullOrBlank()) {
			input_username.setText(accountProperties.username)
		}
	}

	private fun saveChanges() {
		viewModel.setStateEvent(
			AccountStateEvent.UpdateAccountPropertiesEvent(
				input_email.text.toString(),
				input_username.text.toString()
			)
		)
		uiCommunicationListener.hideSoftKeyboard()
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.update_menu, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.save -> {
				saveChanges()
				return true
			}
		}
		return super.onOptionsItemSelected(item)
	}

}






