package com.codingwithmitch.openapi.ui.auth

import android.os.Bundle
import android.view.View
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.auth.state.AuthStateEvent.RegisterAttemptEvent
import com.codingwithmitch.openapi.ui.auth.state.RegistrationFields
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : BaseAuthFragment(R.layout.fragment_register) {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		register_button.setOnClickListener {
			register()
		}
		subscribeObservers()
	}

	fun subscribeObservers() {
		viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
			viewState?.registrationFields?.let { registrationFields ->
				registrationFields.registration_email?.let { input_email.setText(it) }
				registrationFields.registration_username?.let { input_username.setText(it) }
				registrationFields.registration_password?.let { input_password.setText(it) }
				registrationFields.registration_confirm_password?.let {
					input_password_confirm.setText(
						it
					)
				}
			}
		})
	}

	private fun register() {
		viewModel.setStateEvent(
			RegisterAttemptEvent(
				input_email.text.toString(),
				input_username.text.toString(),
				input_password.text.toString(),
				input_password_confirm.text.toString()
			)
		)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		viewModel.setRegistrationFields(
			RegistrationFields(
				input_email.text.toString(),
				input_username.text.toString(),
				input_password.text.toString(),
				input_password_confirm.text.toString()
			)
		)
	}
}