package com.codingwithmitch.openapi.ui.main.account


import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.main.account.state.AccountDataState
import kotlinx.android.synthetic.main.fragment_change_password.*


class ChangePasswordFragment : AccountBaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        update_password_button.setOnClickListener {
            attemptPasswordUpdate()
        }
    }

    override fun observeDataFromChildFragment(accountDataState: AccountDataState) {
        accountDataState.successResponse?.let {
            accountStateChangeListener.hideSoftKeyboard()
            findNavController().popBackStack()
        }
    }

    private fun attemptPasswordUpdate(){
        viewModel.updatePassword(
            input_current_password.text.toString(),
            input_new_password.text.toString(),
            input_confirm_new_password.text.toString()
        )
    }
}





















