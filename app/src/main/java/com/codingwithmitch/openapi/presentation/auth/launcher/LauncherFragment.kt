package com.codingwithmitch.openapi.presentation.auth.launcher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.databinding.FragmentLauncherBinding
import com.codingwithmitch.openapi.presentation.auth.BaseAuthFragment

class LauncherFragment : BaseAuthFragment() {

    private var _binding: FragmentLauncherBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLauncherBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.register.setOnClickListener {
            navRegistration()
        }

        binding.login.setOnClickListener {
            navLogin()
        }

        binding.forgotPassword.setOnClickListener {
            navForgotPassword()
        }

        binding.focusableView.requestFocus() // reset focus
    }

    fun navLogin(){
        findNavController().navigate(R.id.action_launcherFragment_to_loginFragment)
    }

    fun navRegistration(){
        findNavController().navigate(R.id.action_launcherFragment_to_registerFragment)
    }

    fun navForgotPassword(){
        findNavController().navigate(R.id.action_launcherFragment_to_forgotPasswordFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}








