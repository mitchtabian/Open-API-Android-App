package com.codingwithmitch.openapi.repository.main

import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.ui.main.account.state.AccountViewState
import com.codingwithmitch.openapi.util.DataState
import com.codingwithmitch.openapi.util.StateEvent
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

	fun getAccountProperties(
		authToken: AuthToken,
		stateEvent: StateEvent
	): Flow<DataState<out Any?>>

	fun saveAccountProperties(
		authToken: AuthToken,
		email: String,
		username: String,
		stateEvent: StateEvent
	): Flow<DataState<AccountViewState>>

	fun updatePassword(
		authToken: AuthToken,
		currentPassword: String,
		newPassword: String,
		confirmNewPassword: String,
		stateEvent: StateEvent
	): Flow<DataState<AccountViewState>>
}