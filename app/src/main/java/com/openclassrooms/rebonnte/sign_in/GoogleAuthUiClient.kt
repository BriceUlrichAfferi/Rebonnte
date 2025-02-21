package com.openclassrooms.rebonnte.sign_in

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.openclassrooms.rebonnte.R
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CancellationException

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    private val auth = Firebase.auth

    suspend fun signIn(): IntentSender? {
        return try {
            val result = oneTapClient.beginSignIn(builSignInRequest()).await()
            result?.pendingIntent?.intentSender
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            null
        }
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        return try {
            val credential = oneTapClient.getSignInCredentialFromIntent(intent)
            val googleIdToken = credential.googleIdToken
            val googleCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

            val user = auth.signInWithCredential(googleCredential).await().user
            SignInResult(
                data = user?.run {
                    email?.let {
                        Userdata(
                            userId = user.uid,
                            userName = displayName.toString(),
                            email = it,
                            profilePictureUrl = photoUrl?.toString(),
                            photoUrl = photoUrl?.toString()
                        )
                    }
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.localizedMessage
            )
        }
    }



    suspend fun signOut(){
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception){
            if (e is CancellationException) throw  e
        }
    }

    fun getSignedInUser(): Userdata? = auth.currentUser?.run {
        Userdata(
            userId = uid,
            userName = displayName ?: "No Name",
            email = email ?: "No Email",
            profilePictureUrl = photoUrl?.toString() ?: "",
            photoUrl = photoUrl?.toString() ?: ""
        )
    }


    private fun builSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}