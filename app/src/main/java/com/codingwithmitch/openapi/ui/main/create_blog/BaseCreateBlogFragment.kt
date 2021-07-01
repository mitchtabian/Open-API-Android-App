package com.codingwithmitch.openapi.ui.main.create_blog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.UICommunicationListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseCreateBlogFragment
constructor(
	@LayoutRes
	private val layoutRes: Int,
) : Fragment(layoutRes) {

	val TAG: String = "AppDebug"

	val viewModel: CreateBlogViewModel by activityViewModels()

	lateinit var uiCommunicationListener: UICommunicationListener

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setupActionBarWithNavController(R.id.createBlogFragment, activity as AppCompatActivity)
		setupChannel()
	}

	private fun setupChannel() = viewModel.setupChannel()

	fun setupActionBarWithNavController(fragmentId: Int, activity: AppCompatActivity) {
		val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
		NavigationUI.setupActionBarWithNavController(
			activity,
			findNavController(),
			appBarConfiguration
		)
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)
		try {
			uiCommunicationListener = context as UICommunicationListener
		} catch (e: ClassCastException) {
			Log.e(TAG, "$context must implement UICommunicationListener")
		}

	}
}