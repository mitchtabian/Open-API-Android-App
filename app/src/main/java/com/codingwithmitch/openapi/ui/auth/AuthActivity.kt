package com.codingwithmitch.openapi.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.BaseActivity
import com.codingwithmitch.openapi.ui.auth.state.AuthStateEvent.CheckPreviousAuthEvent
import com.codingwithmitch.openapi.ui.main.MainActivity
import com.codingwithmitch.openapi.util.StateMessageCallback
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_auth.*

@AndroidEntryPoint
class AuthActivity : BaseActivity() {
	val viewModel: AuthViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_auth)
		subscribeObservers()
	}

	override fun onResume() {
		super.onResume()
		checkPreviousAuthUser()
	}

	private fun subscribeObservers() {
		viewModel.viewState.observe(this, { viewState ->
			Log.d(TAG, "AuthActivity, subscribeObservers: AuthViewState: $viewState")
			viewState?.authToken?.let {
				sessionManager.login(it)
			}
		})

		viewModel.numActiveJobs.observe(this, {
			displayProgressBar(viewModel.areAnyJobsActive())
		})

		viewModel.stateMessage.observe(this, { stateMessage ->

			stateMessage?.let {

				if (stateMessage.response.message.equals(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE)) {
					onFinishCheckPreviousAuthUser()
				}

				onResponseReceived(
					response = it.response,
					stateMessageCallback = object : StateMessageCallback {
						override fun removeMessageFromStack() {
							viewModel.clearStateMessage()
						}
					}
				)
			}
		})

		sessionManager.cachedToken.observe(this, { token ->
			token.let { authToken ->
				if (authToken != null && authToken.account_pk != -1 && authToken.token != null) {
					navMainActivity()
				}
			}
		})
	}

	private fun onFinishCheckPreviousAuthUser() {
		fragment_container.visibility = View.VISIBLE
		splash_logo.visibility = View.INVISIBLE
	}

	private fun navMainActivity() {
		val intent = Intent(this, MainActivity::class.java)
		startActivity(intent)
		finish()
	}

	private fun checkPreviousAuthUser() {
		viewModel.setStateEvent(CheckPreviousAuthEvent())
	}

	override fun displayProgressBar(isLoading: Boolean) {
		if (isLoading) {
			progress_bar.visibility = View.VISIBLE
		} else {
			progress_bar.visibility = View.GONE
		}
	}

	override fun expandAppBar() {
		// ignore
	}

}