package com.github.p2pfilestream.client.chat

import tornadofx.ItemViewModel
import tornadofx.stringBinding
import tornadofx.toBinding

class ChatModel : ItemViewModel<Chat>() {
    val chatMessages = bind(Chat::chatMessages)
    private val deviceProperty = bind(Chat::peerDeviceProperty)
    val nicknameProperty = bind(Chat::peerNickname)
    val emailProperty = stringBinding(deviceProperty) { value?.account?.username }
    /**
     * Chat is considered "closed" if no chat is selected or it is closed, or it has not yet been opened
     */
    val closed = empty
        .or(bind(Chat::closedProperty).toBinding())
        .or(bind(Chat::openedProperty).toBinding().not())
}
