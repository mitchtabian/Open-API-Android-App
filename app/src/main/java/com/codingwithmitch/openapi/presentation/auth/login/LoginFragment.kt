package com.codingwithmitch.openapi.presentation.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.codingwithmitch.openapi.business.domain.util.StateMessageCallback
import com.codingwithmitch.openapi.databinding.FragmentLoginBinding
import com.codingwithmitch.openapi.presentation.auth.BaseAuthFragment
import com.codingwithmitch.openapi.presentation.util.processQueue

class LoginFragment : BaseAuthFragment() {

    private val viewModel: LoginViewModel by viewModels()

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        binding.loginButton.setOnClickListener {
            cacheState()
            login()
        }
    }

    fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner) { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)
            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(LoginEvents.OnRemoveHeadFromQueue)
                    }
                })
        }
    }

    private fun login(){
        viewModel.onTriggerEvent(LoginEvents.Login(
            email = binding.inputEmail.text.toString(),
            password = binding.inputPassword.text.toString()
        ))
    }

    private fun cacheState(){
        viewModel.onTriggerEvent(LoginEvents.OnUpdateEmail(binding.inputEmail.text.toString()))
        viewModel.onTriggerEvent(LoginEvents.OnUpdatePassword(binding.inputPassword.text.toString()))
    }

    override fun onPause() {
        super.onPause()
        cacheState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}








