package com.templateapp.cloudapi.presentation.main.account.users

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.datasource.cache.task.TaskQueryUtils.Companion.TASK_FILTER_DATE_CREATED
import com.templateapp.cloudapi.business.datasource.cache.task.TaskQueryUtils.Companion.TASK_FILTER_DATE_UPDATED
import com.templateapp.cloudapi.business.datasource.cache.task.TaskQueryUtils.Companion.TASK_FILTER_USERNAME
import com.templateapp.cloudapi.business.datasource.cache.task.TaskQueryUtils.Companion.TASK_ORDER_ASC
import com.templateapp.cloudapi.business.datasource.cache.task.TaskQueryUtils.Companion.TASK_ORDER_DESC
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.databinding.FragmentManageUsersBinding
import com.templateapp.cloudapi.databinding.FragmentTaskBinding
import com.templateapp.cloudapi.presentation.main.account.BaseAccountFragment
import com.templateapp.cloudapi.presentation.main.account.users.detail.ViewAccountEvents
import com.templateapp.cloudapi.presentation.main.task.BaseTaskFragment
import com.templateapp.cloudapi.presentation.main.task.list.TaskEvents
import com.templateapp.cloudapi.presentation.util.TopSpacingItemDecoration
import com.templateapp.cloudapi.presentation.util.processQueue
import kotlinx.coroutines.*

class ManageUsersFragment : BaseAccountFragment(),
    ManageUsersAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener
{

    private var recyclerAdapter: ManageUsersAdapter? = null // can leak memory so need to null
    private val viewModel: ManageUsersViewModel by viewModels()
    private lateinit var menu: Menu

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
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        binding.swipeRefresh.setOnRefreshListener(this)
        initRecyclerView()
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
                })

            recyclerAdapter?.apply {
                submitList(tasksList = state.usersList)
            }
        })
    }


    private  fun resetUI(){
        uiCommunicationListener.hideSoftKeyboard()
        binding.focusableView.requestFocus()
    }

    private fun initRecyclerView(){
        binding.taskRecyclerview.apply {
            layoutManager = LinearLayoutManager(this@ManageUsersFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(10)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = ManageUsersAdapter(this@ManageUsersFragment)
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    Log.d(TAG, "onScrollStateChanged: exhausted? ${viewModel.state.value?.isQueryExhausted}")
                    if (
                        lastPosition == recyclerAdapter?.itemCount?.minus(1)
                        && viewModel.state.value?.isLoading == false
                        && viewModel.state.value?.isQueryExhausted == false
                    ) {
                        Log.d(TAG, "TaskFragment: attempting to load next page...")
                        viewModel.onTriggerEvent(ManageUsersEvents.NextPage)
                    }
                }
            })
            adapter = recyclerAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        inflater.inflate(R.menu.manage_users_menu, this.menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.add -> {

                        findNavController().navigate(R.id.action_manageUsersFragment_to_registerUserFragment)

                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }


    override fun onItemSelected(position: Int, item: Account) {

        try{
            viewModel.state.value?.let { state ->
                    val bundle = bundleOf("accountId" to item._id)
                    findNavController().navigate(R.id.action_manageUsersFragment_to_viewAccountFragment, bundle)
            }?: throw Exception("Null Task")
        }catch (e: Exception){
            e.printStackTrace()
            viewModel.onTriggerEvent(
                ManageUsersEvents.Error(
                stateMessage = StateMessage(
                    response = Response(
                        message = e.message,
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    )
                )
            ))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
        _binding = null
    }


    override fun onRefresh() {
        binding.swipeRefresh.isRefreshing = false
    }

}








