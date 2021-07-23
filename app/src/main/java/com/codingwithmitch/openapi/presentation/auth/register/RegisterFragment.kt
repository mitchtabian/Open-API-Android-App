package com.codingwithmitch.openapi.presentation.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.codingwithmitch.openapi.business.domain.util.StateMessageCallback
import com.codingwithmitch.openapi.databinding.FragmentRegisterBinding
import com.codingwithmitch.openapi.presentation.auth.BaseAuthFragment
import com.codingwithmitch.openapi.presentation.util.processQueue

class RegisterFragment : BaseAuthFragment() {

    private val viewModel: RegisterViewModel by viewModels()

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerButton.setOnClickListener {
            register()
        }
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)
            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(RegisterEvents.OnRemoveHeadFromQueue)
                    }
                })
        }
    }

    private fun setRegisterFields(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ){
        binding.inputEmail.setText(email)
        binding.inputUsername.setText(username)
        binding.inputPassword.setText(password)
        binding.inputPasswordConfirm.setText(confirmPassword)
    }

    private fun cacheState(){
        viewModel.onTriggerEvent(RegisterEvents.OnUpdateEmail(binding.inputEmail.text.toString()))
        viewModel.onTriggerEvent(RegisterEvents.OnUpdateUsername(binding.inputUsername.text.toString()))
        viewModel.onTriggerEvent(RegisterEvents.OnUpdatePassword(binding.inputPassword.text.toString()))
        viewModel.onTriggerEvent(RegisterEvents.OnUpdateConfirmPassword(binding.inputPasswordConfirm.text.toString()))
    }

    private fun register() {
        cacheState()
        viewModel.onTriggerEvent(RegisterEvents.Register(
            email = binding.inputEmail.text.toString(),
            username = binding.inputUsername.text.toString(),
            password = binding.inputPassword.text.toString(),
            confirmPassword = binding.inputPasswordConfirm.text.toString(),
        ))
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