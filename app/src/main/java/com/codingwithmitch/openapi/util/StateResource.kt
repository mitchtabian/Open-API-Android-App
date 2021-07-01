package com.codingwithmitch.openapi.util

import com.codingwithmitch.openapi.ui.AreYouSureCallback

data class StateMessage(val response: Response)

data class Response(
	val message: String?,
	val uiComponentType: UIComponentType,
	val messageType: MessageType
)

sealed class UIComponentType {

	object Toast : UIComponentType()

	object Dialog : UIComponentType()

	class AreYouSureDialog(
		val callback: AreYouSureCallback
	) : UIComponentType()

	object None : UIComponentType()
}

sealed class MessageType {

	object Success : MessageType()

	object Error : MessageType()

	class Info : MessageType()

	class None : MessageType()
}


interface StateMessageCallback {

	fun removeMessageFromStack()
}
