// ui/EditActivity.kt
package com.widget.shared.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.widget.shared.R
import com.widget.shared.ui.edit.EditScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Transparent activity launched from widget tap.
 * Shows a minimal edit UI for note and picture.
 */
@AndroidEntryPoint
class EditActivity : Activity() {
    private val editViewModel: EditViewModel by viewModels()

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { editViewModel.setImageUri(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EditScreen(
                viewModel = editViewModel,
                onPickImage = { imagePickerLauncher.launch("image/*") },
                onClose = { finish() }
            )
        }
    }
}
