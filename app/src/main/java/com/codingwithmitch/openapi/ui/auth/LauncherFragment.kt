package com.codingwithmitch.openapi.ui.auth

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.BuildConfig
import com.codingwithmitch.openapi.R
import kotlinx.android.synthetic.main.fragment_launcher.*

class LauncherFragment : BaseAuthFragment(R.layout.fragment_launcher) {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setDisplayVersion()

		register.setOnClickListener {
			navRegistration()
		}

		login.setOnClickListener {
			navLogin()
		}

		forgot_password.setOnClickListener {
			navForgotPassword()
		}

		focusable_view.requestFocus() // reset focus
	}

	private fun setDisplayVersion() {

		val versionName: String = BuildConfig.VERSION_NAME
		val displayVersion = "Version : $versionName"

		version.text = displayVersion
	}

	fun navLogin() {
		findNavController().navigate(R.id.action_launcherFragment_to_loginFragment)
	}

	fun navRegistration() {
		findNavController().navigate(R.id.action_launcherFragment_to_registerFragment)
	}

	fun navForgotPassword() {
		findNavController().navigate(R.id.action_launcherFragment_to_forgotPasswordFragment)
	}

}








