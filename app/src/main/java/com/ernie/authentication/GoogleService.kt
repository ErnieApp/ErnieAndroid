package com.ernie.authentication

import android.content.Context
import android.content.Intent
import com.ernie.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider

class GoogleService {
    companion object {
        private fun getSignInOptions(context: Context): GoogleSignInOptions {
            return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
        }

        private fun getSignInIntent(context: Context, googleSignInOptions: GoogleSignInOptions): Intent {
            return GoogleSignIn.getClient(context, googleSignInOptions).signInIntent
        }

        fun getSignInIntentWithDefaultSignInOptions(context: Context): Intent {
            return getSignInIntent(context, getSignInOptions(context))
        }

        fun getFireAuthCredentialFromAccount(account: GoogleSignInAccount): AuthCredential {
            return GoogleAuthProvider.getCredential(account.idToken, null)
        }
    }
}