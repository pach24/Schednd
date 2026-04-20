package com.schednd.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepository @Inject constructor(
    private val storage: FirebaseStorage,
    @ApplicationContext private val context: Context
) {
    suspend fun uploadProfilePhoto(userId: String, uri: Uri): String {
        val ref = storage.reference.child("profiles/$userId.jpg")
        val stream = context.contentResolver.openInputStream(uri)
            ?: error("No se pudo abrir la imagen seleccionada")
        stream.use { ref.putStream(it).await() }
        return ref.downloadUrl.await().toString()
    }
}
