package com.github.p2pfilestream.views

import com.github.p2pfilestream.Device
import tornadofx.ItemViewModel
import tornadofx.property
import java.time.LocalDateTime

class Chat(
    val device: Device
) {
    val startProperty = property(LocalDateTime.now())
    var start by startProperty
}

class ChatModel : ItemViewModel<Chat>() {
    val deviceProperty = bind(Chat::device)
}