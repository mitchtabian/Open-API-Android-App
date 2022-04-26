package com.templateapp.cloudapi.presentation.main.account.users

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.util.StateMessageCallback
import com.templateapp.cloudapi.databinding.FragmentManageUsersBinding
import com.templateapp.cloudapi.databinding.FragmentUpdateAccountBinding
import com.templateapp.cloudapi.presentation.main.account.BaseAccountFragment
import com.templateapp.cloudapi.presentation.util.processQueue

class ManageUsersFragment : BaseAccountFragment() {

    private val viewModel: ManageUsersViewModel by viewModels()

    private var _binding: FragmentManageUsersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageUsersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)
            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(ManageUsersEvents.OnRemoveHeadFromQueue)
                    }
                }
            )

           // findNavController().popBackStack(R.id.manageUsersFragment, false)

            /*state.account?.let { account ->
                setAccountDataFields(state.account)
            }*/
        })

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.manage_users_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

            when(item.itemId){
                R.id.add -> {
                    findNavController().navigate(R.id.action_manageUsersFragment_to_registerUserFragment)
                }
            }

        return super.onOptionsItemSelected(item)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}






