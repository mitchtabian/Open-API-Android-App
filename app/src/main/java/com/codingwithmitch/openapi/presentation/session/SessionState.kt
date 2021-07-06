package com.codingwithmitch.openapi.presentation.session

import com.codingwithmitch.openapi.business.domain.models.AuthToken
import com.codingwithmitch.openapi.business.domain.util.Queue
import com.codingwithmitch.openapi.business.domain.util.StateMessage

data class SessionState(
    val isLoading: Boolean = false,
    val authToken: AuthToken? = null,
    val didCheckForPreviousAuthUser: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
