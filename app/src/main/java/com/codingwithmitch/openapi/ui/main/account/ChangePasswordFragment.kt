package com.codingwithmitch.openapi.ui.main.account


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.ui.main.BaseFragment
import com.codingwithmitch.openapi.ui.main.account.state.AccountDataState
import kotlinx.android.synthetic.main.fragment_change_password.*


class ChangePasswordFragment : BaseFragment() {

    private lateinit var accountStateChangeListener: AccountStateChangeListener
    private lateinit var viewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // setup back navigation for this graph
        setupActionBarWithNavController(R.id.accountFragment, activity as AppCompatActivity)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this, providerFactory).get(AccountViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        update_password_button.setOnClickListener {
            attemptPasswordUpdate()
        }

        subscribeObservers()
    }

    private fun subscribeObservers(){
        viewModel.observeDataState().observe(this, Observer {
            // send state to activity for UI updates
            // ex: progress bar and material dialog
            // NOTE: error, loading, successResponse and accountProperties are passed to "accountStateChangeListener"
            //       and action will be taken in MainActivity
            accountStateChangeListener.onAccountDataStateChange(it)

            it.successResponse?.let {
                accountStateChangeListener.hideSoftKeyboard()
                findNavController().popBackStack()
            }

        })

    }

    private fun attemptPasswordUpdate(){
        viewModel.updatePassword(
            input_current_password.text.toString(),
            input_new_password.text.toString(),
            input_confirm_new_password.text.toString()
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            accountStateChangeListener = context as AccountStateChangeListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement OnArticleSelectedListene" )
        }
    }



}





















