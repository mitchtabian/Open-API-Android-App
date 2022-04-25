package com.templateapp.cloudapi.presentation.auth.launcher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.datastore.AppDataStore
import com.templateapp.cloudapi.business.datasource.network.auth.OpenApiAuthService
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.domain.util.DataState
import com.templateapp.cloudapi.business.domain.util.ErrorHandling
import com.templateapp.cloudapi.business.interactors.account.GetAllUsers
import com.templateapp.cloudapi.business.interactors.task.GetOrderAndFilter
import com.templateapp.cloudapi.business.interactors.task.SearchTasks
import com.templateapp.cloudapi.databinding.FragmentLauncherBinding
import com.templateapp.cloudapi.presentation.auth.BaseAuthFragment
import com.templateapp.cloudapi.presentation.auth.registerAdmin.RegisterState
import com.templateapp.cloudapi.presentation.main.account.detail.AccountState
import com.templateapp.cloudapi.presentation.main.task.list.TaskState
import com.templateapp.cloudapi.presentation.main.task.list.TaskViewModel
import com.templateapp.cloudapi.presentation.session.SessionManager
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.properties.Delegates

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








