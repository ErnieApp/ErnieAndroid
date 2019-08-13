package com.ernie.authentication


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ernie.MainActivity
import com.ernie.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupBtnLoginListener()
        setupBtnGoogleLoginListener()
        setupForgotPasswordListener()
        setupNewUserListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            handleGoogleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(data))
        }
    }

    private fun setupBtnLoginListener() {
        btnLogin.setOnClickListener {
            disableButtons()
            val userEmail = fieldEmail.text.toString()
            val userPassword = fieldPassword.text.toString()

            if (isValidEmail(userEmail) && isPasswordBlank(userPassword)) {
                signUserIntoFireAuthWithEmailAndPassword(userEmail, userPassword)
            }
            enableButtonsAfterDelay(LOGIN_ATTEMPT_DELAY_MILLIS)
        }
    }

    private fun setupBtnGoogleLoginListener() {
        btnGoogleLogin.setOnClickListener {
            disableButtons()
            startActivityForResult(GoogleService.getSignInIntentWithDefaultSignInOptions(activity!!.application), RC_GOOGLE_SIGN_IN)
            enableButtonsAfterDelay(LOGIN_ATTEMPT_DELAY_MILLIS)
        }
    }

    private fun setupForgotPasswordListener() {
        forgotPasswordTextView.setOnClickListener {
            val userEmail = fieldEmail.text.toString()
            if (isValidEmail(userEmail)) sendUserPasswordResetEmail(userEmail)
            // Else clear any flagged error with the password field since we are not concerned with it
            else fieldPassword.error = null
        }
    }

    private fun setupNewUserListener() {
        newUserTextView.setOnClickListener {
            val fragmentManager: FragmentManager = activity!!.supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.authenticationFrame, SignUpFragment())
            transaction.commit()
        }
    }

    private fun signUserIntoFireAuthWithEmailAndPassword(userEmail: String, userPassword: String) {
        fireAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener {
                    if (fireAuth.currentUser != null) guideUserHome()
                    else fieldPassword.error = "Incorrect password"
                }
    }

    private fun sendUserPasswordResetEmail(userEmail: String) {
        fireAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener {
            if (it.isSuccessful) Toast.makeText(activity, "Email sent", Toast.LENGTH_LONG).show()
            else Toast.makeText(activity, "Error = " + it.exception, Toast.LENGTH_LONG).show()
        }
    }

    private fun isPasswordBlank(userPassword: String): Boolean {
        return if (userPassword.isBlank()) {
            fieldPassword.error = "Enter your password"
            true
        } else {
            false
        }
    }

    private fun isValidEmail(userEmail: CharSequence): Boolean {
        return if (userEmail.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            true
        } else {
            if (userEmail.isBlank()) fieldEmail.error = "Enter your email"
            else fieldEmail.error = "Invalid email"
            false
        }
    }

    private fun guideUserHome() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        activity!!.finish()
    }

    private fun disableButtons() {
        btnLogin.isEnabled = false
        btnGoogleLogin.isEnabled = false
    }

    private fun enableButtonsAfterDelay(delayMillis: Long) {
        Handler().postDelayed({
            if (btnLogin != null && btnGoogleLogin != null) {
                btnLogin.isEnabled = true
                btnGoogleLogin.isEnabled = true
            }
        }, delayMillis)
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            signIntoFireAuthWithGoogleAccount(account!!)
        } catch (e: ApiException) {
            Log.e(TAG, "Google sign in failed")
            Log.w(TAG, "signInResult: failed code =" + e.statusCode)
        }

    }

    private fun signIntoFireAuthWithGoogleAccount(account: GoogleSignInAccount) {
        val credential = GoogleService.getFireAuthCredentialFromAccount(account)

        fireAuth.fetchSignInMethodsForEmail(account.email!!)
                .addOnCompleteListener { task ->
                    //TODO: Catch network exception which is thrown when there is no internet
                    val isNewUser = task.result!!.signInMethods!!.isEmpty()

                    if (isNewUser) {
                        RegistrationFormFragment.launchRegistrationFormWithGoogleAccount(activity!!, credential, account)
                    } else {
                        signUserIntoFireAuthWithCredential(credential)
                    }
                }
    }

    private fun signUserIntoFireAuthWithCredential(credential: AuthCredential) {
        fireAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) guideUserHome()
                    else Log.e(TAG, "signInWithCredential:failure + ", it.exception)
                }
    }

    companion object {
        private const val RC_GOOGLE_SIGN_IN = 444
        private const val LOGIN_ATTEMPT_DELAY_MILLIS: Long = 5000
        private const val TAG = "LoginFragment"
        private val fireAuth: FirebaseAuth = FirebaseAuth.getInstance()
    }
}
