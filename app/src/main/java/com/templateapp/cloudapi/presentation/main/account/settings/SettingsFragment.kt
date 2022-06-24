package com.templateapp.cloudapi.presentation.main.account.settings

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.util.StateMessageCallback
import com.templateapp.cloudapi.databinding.FragmentAccountBinding
import com.templateapp.cloudapi.databinding.FragmentSettingsBinding
import com.templateapp.cloudapi.presentation.main.account.BaseAccountFragment
import com.templateapp.cloudapi.presentation.util.processQueue
import kotlinx.android.synthetic.main.fragment_account.*

class SettingsFragment : BaseAccountFragment() {

    private val viewModel: SettingsViewModel by viewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)


        binding.changePassword.setOnClickListener{
            findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
        }

        binding.logoutButton.setOnClickListener {
            viewModel.onTriggerEvent(SettingsEvents.Logout)
        }


        subscribeObservers()
        viewModel.onTriggerEvent(SettingsEvents.GetAccount)

    }



    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner) { state ->

            //uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(SettingsEvents.OnRemoveHeadFromQueue)
                    }
                })

            state.account?.let { account ->
                setAccountDataFields(account)
            }
        }
    }

    private fun setAccountDataFields(account: Account){
        binding.email.text = account.email
        binding.username.text = account.name
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_view_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.edit -> {
                viewModel.state.value?.let { state ->
                    state.account?.let { account ->
                        val bundle = bundleOf("accountId" to account._id)
                        findNavController().navigate(R.id.action_accountFragment_to_updateAccountFragment, bundle)
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}