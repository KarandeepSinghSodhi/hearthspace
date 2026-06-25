// ui/edit/EditScreen.kt
package com.lovewidget.ui.edit

import android.graphics.BitmapFactory
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
import com.lovewidget.R
import com.lovewidget.ui.edit.EditViewModel

@Composable
fun EditScreen(
    viewModel: EditViewModel,
    onPickImage: () -> Unit,
    onClose: () -> Unit
) {
    val note by viewModel.note.collectAsState()
    val imageUri by viewModel.imageUri.collectAsState()
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_note_hint)) },
                actions = {
                    IconButton(onClick = onClose) {
                        Icon(painterResource(id = R.drawable.ic_close), contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.save() }) {
                Icon(painterResource(id = R.drawable.ic_save), contentDescription = null)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = note,
                onValueChange = { viewModel.updateNote(it) },
                label = { Text(stringResource(R.string.edit_note_hint)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
            )
            Spacer(modifier = Modifier.height(16.dp))
            imageUri?.let { uri ->
                // Load bitmap efficiently (placeholder simple loading)
                val stream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(stream)
                stream?.close()
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onPickImage) {
                Text(text = "Pick Image")
            }
        }
    }
}
