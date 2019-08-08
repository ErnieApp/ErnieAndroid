package com.ernie

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import com.ernie.login.introSlide1Fragment
import com.ernie.login.introSlide2Fragment
import com.ernie.login.introSlide3Fragment
import com.ernie.login.introSlide4Fragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.github.paolorotolo.appintro.AppIntro
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.appintro_layout.*

class LoginActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(introSlide1Fragment.newInstance())
        addSlide(introSlide2Fragment.newInstance())
        addSlide(introSlide3Fragment.newInstance())
        addSlide(introSlide4Fragment.newInstance())

        setIndicatorColor(R.color.colorIntroSelectedIndicator, R.color.colorIntroUnselectedIndicator)

        val previousButton = findViewById<ImageButton>(com.github.paolorotolo.appintro.R.id.back)
        previousButton.setColorFilter(R.color.colorIntroText)
        setNextArrowColor(R.color.colorIntroText)

        showSkipButton(false)
        wizardMode = true
        backButtonVisibilityWithDone = true

        get_started.setOnClickListener {
            createSignInIntent()
        }

        log_in.setOnClickListener {
            createSignInIntent()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.appintro_layout
    }

    private fun createSignInIntent() {

        // [START auth_fui_create_intent]

        // Choose authentication providers

        val providers = arrayListOf(

                AuthUI.IdpConfig.EmailBuilder().build(),

                AuthUI.IdpConfig.PhoneBuilder().build(),

                AuthUI.IdpConfig.GoogleBuilder().build())


        // Create and launch sign-in intent

        startActivityForResult(

                AuthUI.getInstance()

                        .createSignInIntentBuilder()

                        .setAvailableProviders(providers)

                        .setLogo(R.drawable.ernie_logo)

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

    // [END auth_fui_result]


    private fun delete() {

        // [START auth_fui_delete]

        AuthUI.getInstance()

                .delete(this)

                .addOnCompleteListener {

                    // ...

                }

        // [END auth_fui_delete]

    }

    private fun privacyAndTerms() {

        val providers = emptyList<AuthUI.IdpConfig>()

        // [START auth_fui_pp_tos]

        startActivityForResult(

                AuthUI.getInstance()

                        .createSignInIntentBuilder()

                        .setAvailableProviders(providers)

                        .setTosAndPrivacyPolicyUrls(

                                "https://example.com/terms.html",

                                "https://example.com/privacy.html")

                        .build(),

                RC_SIGN_IN)

        // [END auth_fui_pp_tos]

    }


    companion object {


        private const val RC_SIGN_IN = 123

    }

}
