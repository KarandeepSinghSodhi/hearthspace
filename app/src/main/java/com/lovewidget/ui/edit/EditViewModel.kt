// ui/edit/EditViewModel.kt
package com.lovewidget.ui.edit

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lovewidget.data.model.PetInfo
import com.lovewidget.data.repository.SharedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val sharedRepo: SharedRepository
) : ViewModel() {
    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    init {
        // Load current cached item into UI
        viewModelScope.launch {
            sharedRepo.cachedItem?.let { item ->
                _note.value = item.note
                // Image URI will be loaded from cache later if needed
            }
        }
    }

    fun updateNote(newNote: String) {
        _note.value = newNote
    }

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
    }

    /**
     * Called when user taps Save.
     * It enqueues a WorkManager job to upload image (if any) and update Firestore.
     */
    fun save() {
        viewModelScope.launch {
            val noteText = _note.value
            val imageUri = _imageUri.value
            // WorkManager will handle uploading image and updating Firestore.
            // Here we just trigger the worker with appropriate input data.
            com.lovewidget.work.SyncEditWorker.enqueue(noteText, imageUri?.toString())
        }
    }
}
