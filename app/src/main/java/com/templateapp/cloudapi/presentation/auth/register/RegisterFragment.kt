package com.templateapp.cloudapi.presentation.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.domain.models.Role
import com.templateapp.cloudapi.business.domain.util.StateMessageCallback
import com.templateapp.cloudapi.databinding.FragmentRegisterBinding
import com.templateapp.cloudapi.presentation.auth.BaseAuthFragment
import com.templateapp.cloudapi.presentation.main.account.users.update.ChangeAccountEvents
import com.templateapp.cloudapi.presentation.util.processQueue

class RegisterFragment : BaseAuthFragment() {

    private val viewModel: RegisterViewModel by viewModels()

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var role: Role;
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
        viewModel.state.observe(viewLifecycleOwner, { state ->
            //uiCommunicationListener.displayProgressBar(state.isLoading)
            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(RegisterEvents.OnRemoveHeadFromQueue)
                    }
                }
            )

            if(state.isComplete){
                findNavController().popBackStack(R.id.accountFragment, false)
                Toast.makeText(context ,"You have successfully sent an email", Toast.LENGTH_SHORT).show();
            }
            state.roles?.let { roles ->
                setAccountDataFieldsRoles(state.roles)
            }

        })

    }

    private fun setAccountDataFieldsRoles(roles: List<Role>){

        if(roles.size!=0) {
            role = roles[0];
        }
        if (roles != null) {

            val adapter = activity?.let {
                ArrayAdapter<Role>(
                    it,
                    R.layout.simple_spinner_item,
                    roles
                )
            }
            if (adapter != null) {
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            }
            binding.roleSpinner!!.setAdapter(adapter)
            for(i in 0 until roles.size step 1){
                if(role.title.equals(roles[i].title)){


                    binding.roleSpinner.setSelection(i);

                }
            }



        }
    }


    private fun cacheState(){
        viewModel.onTriggerEvent(RegisterEvents.OnUpdateEmail(binding.inputEmail.text.toString()))
       // viewModel.onTriggerEvent(RegisterEvents.OnUpdateRole(binding.roleSpinner.selectedItem.toString()))
    }

    private fun register() {

        print("eeee")
        cacheState()

        print("ssssaaa")
        viewModel.onTriggerEvent(RegisterEvents.Registration(
            email = binding.inputEmail.text.toString(),
            role = binding.roleSpinner.selectedItem.toString(),
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