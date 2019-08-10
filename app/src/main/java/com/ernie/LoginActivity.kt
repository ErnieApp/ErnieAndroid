package com.ernie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    val fireAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener {

            val userEmail = fieldEmail.text.toString()
            val userPassword = fieldPassword.text.toString()

            if (userEmail.isNotBlank() && userPassword.isNotBlank()) {
                fireAuth.signInWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener {
                            // Check if user has logged in successfully
                            if (fireAuth.currentUser != null) {
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                                finish()
                            } else {
                                invalidCredentialsTextView.visibility = View.VISIBLE
                            }
                        }
            }
        }

        btnGoogleLogin.setOnClickListener {

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

            // Configure Google Sign In Initialization
            val googleSignInClient = GoogleSignIn.getClient(this, gso)

            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)

        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.e("JKE", "Google sign in success : " + account!!)

        } catch (e: ApiException) {
            Log.e("JKE", "Google sign in failed")
            Log.w("JKE", "signInResult:failed code=" + e.statusCode)
        }

    }



    private fun createSignInIntent() {

        val customLayout: AuthMethodPickerLayout = AuthMethodPickerLayout
                .Builder(R.layout.activity_login)
                .setGoogleButtonId(R.id.btnGoogleLogin)
//                .setTosAndPrivacyPolicyId(R.id.baz)
                .build()

        // [START auth_fui_create_intent]

        // Choose authentication providers

        val providers = arrayListOf(

//                AuthUI.IdpConfig.EmailBuilder().build(),

//                AuthUI.IdpConfig.PhoneBuilder().build(),

                AuthUI.IdpConfig.GoogleBuilder().build())


        // Create and launch sign-in intent

        startActivityForResult(

                AuthUI.getInstance()

                        .createSignInIntentBuilder()

                        .setAvailableProviders(providers)

                        .setAuthMethodPickerLayout(customLayout)

                        .setTosAndPrivacyPolicyUrls("google.com", "yahoo.co.uk")

                        .build(),

                RC_SIGN_IN)

        // [END auth_fui_create_intent]

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)

        }
    }

    companion object {
        private const val RC_SIGN_IN = 123
    }
}
