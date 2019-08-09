package com.ernie

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    private fun createSignInIntent() {

        val customLayout: AuthMethodPickerLayout = AuthMethodPickerLayout
                .Builder(R.layout.activity_login)
                .setGoogleButtonId(R.id.google_login_button)
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


    // [START auth_fui_result]

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)



        if (requestCode == RC_SIGN_IN) {

            val response = IdpResponse.fromResultIntent(data)



            if (resultCode == Activity.RESULT_OK) {

                // Successfully signed in

                val user = FirebaseAuth.getInstance().currentUser

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                finish()

            } else {

                // Sign in failed. If response is null the user canceled the

                // sign-in flow using the back button. Otherwise check

                // response.getError().getErrorCode() and handle the error.

                // ...

            }

        }

    }

    companion object {
        private const val RC_SIGN_IN = 123
    }
}
