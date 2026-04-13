package com.schednd.model

import com.google.firebase.Timestamp

data class Event(
    val code: String = "",
    val name: String = "",
    val creatorId: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
