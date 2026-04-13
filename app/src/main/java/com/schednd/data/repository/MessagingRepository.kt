package com.schednd.data.repository

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagingRepository @Inject constructor(
    private val messaging: FirebaseMessaging
) {
    suspend fun subscribeToEvent(code: String) {
        messaging.subscribeToTopic("event_$code").await()
    }

    suspend fun unsubscribeFromEvent(code: String) {
        messaging.unsubscribeFromTopic("event_$code").await()
    }
}
