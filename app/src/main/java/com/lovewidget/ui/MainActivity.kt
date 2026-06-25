package com.lovewidget.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.lovewidget.R
import com.lovewidget.auth.AuthManager
import com.lovewidget.ui.pairing.PairingScreen
import com.lovewidget.utils.UpdateChecker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var authManager: AuthManager

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        lifecycleScope.launch {
            val user = authManager.handleSignInResult(result.data)
            if (user != null) showPairingScreen()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (authManager.currentUser() != null) {
            showPairingScreen()
        } else {
            showSignInScreen()
        }
        lifecycleScope.launch { UpdateChecker.checkForUpdate(this@MainActivity) }
    }

    private fun showSignInScreen() = setContent {
        MaterialTheme {
            SignInContent(onSignIn = { signInLauncher.launch(authManager.getSignInIntent()) })
        }
    }

    private fun showPairingScreen() = setContent {
        MaterialTheme { PairingScreen() }
    }
}

@Composable
private fun SignInContent(onSignIn: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFCE4EC), Color(0xFFFFFFFF))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text("❤️", fontSize = 64.sp)
            Text(
                "Shared Love Widget",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE91E8C)
            )
            Text(
                "A tiny shared place on two home screens.",
                fontSize = 15.sp,
                color = Color(0xFF888888),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E8C))
            ) {
                Text(stringResource(R.string.sign_in_button), fontSize = 16.sp, color = Color.White)
            }
        }
    }
}
