// ui/pairing/PairingScreen.kt
package com.lovewidget.ui.pairing

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lovewidget.R
import com.lovewidget.ui.EditActivity
import com.lovewidget.utils.QRCodeGenerator

@Composable
fun PairingScreen(viewModel: PairingViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val pairId by viewModel.pairId.collectAsState()
    val joinId by viewModel.joinId.collectAsState()
    val qrBitmap by viewModel.qrBitmap.collectAsState()
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            // Simple settings sheet (logout)
            Button(
                onClick = { viewModel.logout() },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(stringResource(R.string.logout))
            }
        },
        sheetPeekHeight = 0.dp
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (pairId == null) {
                // Create new pair
                Button(onClick = { viewModel.createPair() }) {
                    Text(stringResource(R.string.pairing_title))
                }
            } else {
                Text(text = "Pair ID: ${pairId}")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Pair ID", pairId))
                        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                    }) {
                        Text(stringResource(R.string.copy_pair_id))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        viewModel.generateQr(pairId!!)
                    }) {
                        Text(stringResource(R.string.share_pair_qr))
                    }
                }
                qrBitmap?.let { bmp ->
                    Image(bmp.asImageBitmap(), contentDescription = null, modifier = Modifier.size(200.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))
                // Join existing
                OutlinedTextField(
                    value = joinId,
                    onValueChange = { viewModel.setJoinId(it) },
                    label = { Text(stringResource(R.string.pair_id_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.joinPair() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Join Pair")
                }
            }
        }
    }
}
