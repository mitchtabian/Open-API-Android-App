package com.codingwithmitch.openapi.ui.main.account

import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.repository.main.AccountRepositoryImpl
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.BaseViewModel
import com.codingwithmitch.openapi.ui.main.account.state.AccountStateEvent.*
import com.codingwithmitch.openapi.ui.main.account.state.AccountViewState
import com.codingwithmitch.openapi.util.*
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.INVALID_STATE_EVENT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class AccountViewModel
@Inject
constructor(
	val sessionManager: SessionManager,
	val accountRepository: AccountRepositoryImpl
) : BaseViewModel<AccountViewState>() {

	override fun handleNewData(data: AccountViewState) {
		data.accountProperties?.let { accountProperties ->
			setAccountPropertiesData(accountProperties)
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun setStateEvent(stateEvent: StateEvent) {
		sessionManager.cachedToken.value?.let { authToken ->
			val job: Flow<DataState<out Any?>> = when (stateEvent) {

				is GetAccountPropertiesEvent -> {
					accountRepository.getAccountProperties(
						stateEvent = stateEvent,
						authToken = authToken
					)
				}

				is UpdateAccountPropertiesEvent -> {
					accountRepository.saveAccountProperties(
						stateEvent = stateEvent,
						authToken = authToken,
						email = stateEvent.email,
						username = stateEvent.username
					)
				}

				is ChangePasswordEvent -> {
					accountRepository.updatePassword(
						stateEvent = stateEvent,
						authToken = authToken,
						currentPassword = stateEvent.currentPassword,
						newPassword = stateEvent.newPassword,
						confirmNewPassword = stateEvent.confirmNewPassword
					)
				}

				else -> {
					flow {
						emit(
							DataState.error(
								response = Response(
									message = INVALID_STATE_EVENT,
									uiComponentType = UIComponentType.None,
									messageType = MessageType.Error
								),
								stateEvent = stateEvent
							)
						) as DataState<AccountViewState>
					}
				}
			}
			launchJob(stateEvent, job as Flow<DataState<AccountViewState>>)
		}
	}

	private fun setAccountPropertiesData(accountProperties: AccountProperties) {
		val update = getCurrentViewStateOrNew()
		if (update.accountProperties == accountProperties) {
			return
		}
		update.accountProperties = accountProperties
		setViewState(update)
	}

	override fun initNewViewState(): AccountViewState {
		return AccountViewState()
	}

	fun logout() {
		sessionManager.logout()
	}

	override fun onCleared() {
		super.onCleared()
		cancelActiveJobs()
	}

}















