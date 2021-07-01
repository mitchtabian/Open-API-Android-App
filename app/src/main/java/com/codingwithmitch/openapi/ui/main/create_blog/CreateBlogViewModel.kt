package com.codingwithmitch.openapi.ui.main.create_blog

import android.net.Uri
import com.codingwithmitch.openapi.repository.main.CreateBlogRepositoryImpl
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.BaseViewModel
import com.codingwithmitch.openapi.ui.main.create_blog.state.CreateBlogStateEvent.CreateNewBlogEvent
import com.codingwithmitch.openapi.ui.main.create_blog.state.CreateBlogViewState
import com.codingwithmitch.openapi.ui.main.create_blog.state.CreateBlogViewState.NewBlogFields
import com.codingwithmitch.openapi.util.*
import com.codingwithmitch.openapi.util.ErrorHandling.Companion.INVALID_STATE_EVENT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class CreateBlogViewModel
@Inject
constructor(
	private val createBlogRepository: CreateBlogRepositoryImpl,
	val sessionManager: SessionManager
) : BaseViewModel<CreateBlogViewState>() {


	override fun handleNewData(data: CreateBlogViewState) {

		setNewBlogFields(
			data.blogFields.newBlogTitle,
			data.blogFields.newBlogBody,
			data.blogFields.newImageUri
		)
	}

	override fun setStateEvent(stateEvent: StateEvent) {
		sessionManager.cachedToken.value?.let { authToken ->
			val job: Flow<DataState<CreateBlogViewState>> = when (stateEvent) {

				is CreateNewBlogEvent -> {
					val title = RequestBody.create(
						MediaType.parse("text/plain"),
						stateEvent.title
					)
					val body = RequestBody.create(
						MediaType.parse("text/plain"),
						stateEvent.body
					)

					createBlogRepository.createNewBlogPost(
						stateEvent = stateEvent,
						authToken = authToken,
						title = title,
						body = body,
						image = stateEvent.image
					)
				}

				else -> {
					flow {
						emit(
							DataState.error<CreateBlogViewState>(
								response = Response(
									message = INVALID_STATE_EVENT,
									uiComponentType = UIComponentType.None,
									messageType = MessageType.Error
								),
								stateEvent = stateEvent
							)
						)
					}
				}
			}
			launchJob(stateEvent, job)
		}
	}

	override fun initNewViewState(): CreateBlogViewState {
		return CreateBlogViewState()
	}

	fun setNewBlogFields(title: String?, body: String?, uri: Uri?) {
		val update = getCurrentViewStateOrNew()
		val newBlogFields = update.blogFields
		title?.let { newBlogFields.newBlogTitle = it }
		body?.let { newBlogFields.newBlogBody = it }
		uri?.let { newBlogFields.newImageUri = it }
		update.blogFields = newBlogFields
		setViewState(update)
	}

	fun clearNewBlogFields() {
		val update = getCurrentViewStateOrNew()
		update.blogFields = NewBlogFields()
		setViewState(update)
	}

	override fun onCleared() {
		super.onCleared()
		cancelActiveJobs()
	}

}





