package com.codingwithmitch.openapi.ui.auth

import android.os.Bundle
import android.view.View
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.auth.state.AuthStateEvent.LoginAttemptEvent
import com.codingwithmitch.openapi.ui.auth.state.LoginFields
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseAuthFragment(R.layout.fragment_login) {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		subscribeObservers()

		login_button.setOnClickListener {
			login()
		}

	}

	fun subscribeObservers() {
		viewModel.viewState.observe(viewLifecycleOwner, { authViewState ->
			authViewState?.loginFields?.let { loginFields ->
				loginFields.login_email?.let { it -> input_email.setText(it) }
				loginFields.login_password?.let { input_password.setText(it) }
			}
		})
	}

	fun login() {
		saveLoginFields()
		viewModel.setStateEvent(
			LoginAttemptEvent(
				input_email.text.toString(),
				input_password.text.toString()
			)
		)
	}

	private fun saveLoginFields() {
		viewModel.setLoginFields(
			LoginFields(
				input_email.text.toString(),
				input_password.text.toString()
			)
		)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		saveLoginFields()
	}

}