package com.codingwithmitch.openapi.ui.main.account

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.BuildConfig
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.codingwithmitch.openapi.ui.main.account.state.AccountStateEvent.GetAccountPropertiesEvent
import com.codingwithmitch.openapi.ui.main.account.state.AccountViewState
import com.codingwithmitch.openapi.util.StateMessageCallback
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.coroutines.FlowPreview

@FlowPreview
class AccountFragment : BaseAccountFragment(R.layout.fragment_account) {

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

		change_password.setOnClickListener {
			findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
		}

		logout_button.setOnClickListener {
			viewModel.logout()
		}

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

	override fun onResume() {
		super.onResume()
		viewModel.setStateEvent(GetAccountPropertiesEvent())
	}

	private fun setAccountDataFields(accountProperties: AccountProperties) {

		val versionName: String = BuildConfig.VERSION_NAME
		val displayVersion = "Version : $versionName"

		email?.text = accountProperties.email
		username?.text = accountProperties.username
		version?.text = displayVersion
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.edit_view_menu, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.edit -> {
				findNavController().navigate(R.id.action_accountFragment_to_updateAccountFragment)
				return true
			}
		}
		return super.onOptionsItemSelected(item)
	}

}