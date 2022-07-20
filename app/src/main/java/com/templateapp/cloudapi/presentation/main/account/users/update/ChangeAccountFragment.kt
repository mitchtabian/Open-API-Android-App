package com.templateapp.cloudapi.presentation.main.account.users.update

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.InverseBindingListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.Role
import com.templateapp.cloudapi.business.domain.util.DataState
import com.templateapp.cloudapi.business.domain.util.ErrorHandling
import com.templateapp.cloudapi.business.domain.util.MessageType
import com.templateapp.cloudapi.business.domain.util.StateMessageCallback
import com.templateapp.cloudapi.databinding.FragmentChangeAccountBinding
import com.templateapp.cloudapi.databinding.FragmentUpdateAccountBinding
import com.templateapp.cloudapi.presentation.main.account.BaseAccountFragment
import com.templateapp.cloudapi.presentation.main.account.users.ManageUsersAdapter
import com.templateapp.cloudapi.presentation.main.account.users.ManageUsersEvents
import com.templateapp.cloudapi.presentation.util.processQueue
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ChangeAccountFragment : BaseAccountFragment() {

    private val viewModel: ChangeAccountViewModel by viewModels()
    private var _binding: FragmentChangeAccountBinding? = null
    private val binding get() = _binding!!

    private var email: String = ""
    private lateinit var role: Role;
    private var name: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeAccountBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->
            //uiCommunicationListener.displayProgressBar(state.isLoading)
            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(ChangeAccountEvents.OnRemoveHeadFromQueue)
                    }
                }
            )
            if(state.isUpdateComplete){
                findNavController().popBackStack(R.id.accountFragment, false)
            }

            state.account?.let { account ->
                setAccountDataFields(state.account)
            }
            state.roles?.let { roles ->
                setAccountDataFieldsRoles(state.roles)
             }
        })

    }


    private fun setAccountDataFields(account: Account){

        email = account.email
        name = account.name
        role = account.role
        binding.email.setText(account.email)
        binding.username.setText(account.name)
        binding.age.setText(account.age.toString())
        binding.enabled.setChecked(account.enabled)
        var createdAtList: List<String> = account.createdAt.split('T')
        var updatedAtList: List<String> = account.updatedAt.split('T')


        var createdAtTime = createdAtList[1].split(':')
        var updatedAtTime = updatedAtList[1].split(':')

        var createdAt = createdAtList[0] + " " + createdAtTime[0] + ":" + createdAtTime[1]
        var updatedAt = updatedAtList[0] + " " + updatedAtTime[0] + ":" + updatedAtTime[1]

        binding.createdAt.setText(createdAt)
        binding.updatedAt.setText(updatedAt)

    }

    private fun setAccountDataFieldsRoles(roles: List<Role>){


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

    private fun saveChanges(){
       // var role: Role = Role("625d59e2949d171c2c0bb52b", "User")

        viewModel.onTriggerEvent(ChangeAccountEvents.Update(
            email = binding.email.text.toString(),
            username = binding.username.text.toString(),
            age = Integer.parseInt(binding.age.text.toString()),
            enabled = binding.enabled.isChecked,
            role = binding.roleSpinner.selectedItem.toString(),
            initEmail = email,
            initName = name

        ))
        uiCommunicationListener.hideSoftKeyboard()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save -> {
                cacheState()
                saveChanges()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun cacheState(){
        val email = binding.email.text.toString()
        val username = binding.username.text.toString()
        val age = binding.age.text.toString()
        val enabled = binding.enabled.text.toString()
        viewModel.onTriggerEvent(ChangeAccountEvents.OnUpdateEmail(email))
        viewModel.onTriggerEvent(ChangeAccountEvents.OnUpdateUsername(username))
        viewModel.onTriggerEvent(ChangeAccountEvents.OnUpdateAge(Integer.parseInt(age)))
        viewModel.onTriggerEvent(ChangeAccountEvents.OnUpdateEnabled(Boolean.equals(enabled)))
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






