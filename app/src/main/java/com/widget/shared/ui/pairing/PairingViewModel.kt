// ui/pairing/PairingViewModel.kt
package com.widget.shared.ui.pairing

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.widget.shared.data.repository.PairingRepository
import com.widget.shared.data.repository.SharedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PairingViewModel @Inject constructor(
    private val pairingRepo: PairingRepository,
    private val sharedRepo: SharedRepository
) : ViewModel() {
    private val _pairId = MutableStateFlow<String?>(null)
    val pairId: StateFlow<String?> = _pairId

    private val _joinId = mutableStateOf("")
    val joinId: String get() = _joinId.value

    private val _qrBitmap = MutableStateFlow<android.graphics.Bitmap?>(null)
    val qrBitmap: StateFlow<android.graphics.Bitmap?> = _qrBitmap

    init {
        // Attempt to restore existing pairing for the logged‑in user
        viewModelScope.launch {
            val existing = pairingRepo.getPairingForCurrentUser()
            existing?.let {
                _pairId.value = it.pairId
                sharedRepo.setPairId(it.pairId)
            }
        }
    }

    fun createPair() {
        viewModelScope.launch {
            val id = pairingRepo.createPairing()
            id?.let {
                _pairId.value = it
                sharedRepo.setPairId(it)
            }
        }
    }

    fun setJoinId(value: String) {
        _joinId.value = value
    }

    fun joinPair() {
        val id = _joinId.value
        if (id.isBlank()) return
        viewModelScope.launch {
            val success = pairingRepo.joinPairing(id)
            if (success) {
                _pairId.value = id
                sharedRepo.setPairId(id)
            }
        }
    }

    fun generateQr(pairId: String) {
        viewModelScope.launch {
            val bmp = com.lovewidget.utils.QRCodeGenerator.generate(pairId)
            _qrBitmap.value = bmp
        }
    }

    fun logout() {
        // Sign out via AuthManager (injected elsewhere) – simple placeholder
        // In real code you would call AuthManager.signOut()
    }
}
