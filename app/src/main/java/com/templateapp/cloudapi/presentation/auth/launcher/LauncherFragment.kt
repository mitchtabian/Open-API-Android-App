package com.templateapp.cloudapi.presentation.auth.launcher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.databinding.FragmentLauncherBinding
import com.templateapp.cloudapi.presentation.auth.BaseAuthFragment
import com.templateapp.cloudapi.presentation.auth.register.RegisterState

class LauncherFragment: BaseAuthFragment() {
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
    val state: MutableLiveData<RegisterState> = MutableLiveData(RegisterState())



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //search()

       /* val Client = UDP_Client()
        Client.Message = "Your message"
        Client.NachrichtSenden()*/
        var openApiMainService: OpenApiMainService



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

    fun navForgotPassword(){
        findNavController().navigate(R.id.action_launcherFragment_to_forgotPasswordFragment)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}








