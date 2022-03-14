package com.nznlabs.familymap240.util

data class Message(val response: Response)

data class Response(
    var message: String?,
    val uiComponentType: UIComponentType,
    val messageType: MessageType,
    val title: String? = null
)

sealed class UIComponentType {

    object Dialog : UIComponentType()

    object Toast : UIComponentType()

    class CancelContinueDialog(val callback: CancelContinueCallback) : UIComponentType()

    object None : UIComponentType()

}

sealed class MessageType {

    object Success : MessageType()

    object Error : MessageType()

    object Info : MessageType()

    object Title : MessageType()

    object None : MessageType()
}


interface MessageCallback {
    fun removeMessage()

    fun customCallback() {} // default customCallback is null
}


interface CancelContinueCallback {
    fun proceed()
    fun cancel() { /*Default implementation - do nothing */}
}
