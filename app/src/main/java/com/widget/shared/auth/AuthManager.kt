// auth/AuthManager.kt
package com.widget.shared.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles authentication flow.
 * Primary method: Google Sign‑In.
 * Fallback (if Google Play services unavailable) can be added later – for now we expose a simple
 * "signIn" that returns the FirebaseUser or null.
 */
@Singleton
class AuthManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth
) {
    private val TAG = "AuthManager"
    private val googleClient: GoogleSignInClient by lazy { createGoogleClient() }

    private fun createGoogleClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(com.widget.shared.R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    /**
     * Starts the Google sign‑in intent. Caller should register an ActivityResultLauncher with
     * `ActivityResultContracts.StartActivityForResult()` and feed the result to `handleSignInResult`.
     */
    fun getSignInIntent(): Intent = googleClient.signInIntent

    /**
     * Process the result from Google sign‑in.
     */
    suspend fun handleSignInResult(data: Intent?): FirebaseUser? {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
            firebaseAuthWithGoogle(account)
        } catch (e: Exception) {
            Log.e(TAG, "Google sign‑in failed", e)
            null
        }
    }

    private suspend fun firebaseAuthWithGoogle(account: GoogleSignInAccount?): FirebaseUser? {
        if (account == null) return null
        val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        return try {
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            authResult.user
        } catch (e: Exception) {
            Log.e(TAG, "Firebase auth with Google failed", e)
            null
        }
    }

    fun currentUser(): FirebaseUser? = firebaseAuth.currentUser

    fun signOut() {
        firebaseAuth.signOut()
        googleClient.signOut()
    }
}
