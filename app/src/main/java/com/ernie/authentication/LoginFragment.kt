package com.ernie.authentication


import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.ernie.AppDatabase
import com.ernie.MainActivity
import com.ernie.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
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

            if (isValidEmail(userEmail) && !isPasswordBlank(userPassword)) {
                signUserIntoFireAuthWithEmailAndPassword(userEmail, userPassword)
            } else {
                enableButtons()
            }
        }
    }

    private fun setupBtnGoogleLoginListener() {
        btnGoogleLogin.setOnClickListener {
            disableButtons()
            startActivityForResult(GoogleService.getSignInIntentWithDefaultSignInOptions(activity!!.application), RC_GOOGLE_SIGN_IN)
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
            SignUpFragment.launchSignUp(activity!!)
        }
    }

    private fun signUserIntoFireAuthWithEmailAndPassword(userEmail: String, userPassword: String) {
        AppDatabase.getAuthInstance()!!.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener {
                    if (AppDatabase.getAuthInstance()!!.currentUser != null) MainActivity.launchMainActivityAsFreshStart(activity!!)
                    else {
                        fieldPassword.error = "Incorrect password"
                        enableButtons()
                    }
                }
    }

    private fun signUserIntoFireAuthWithCredential(credential: AuthCredential) {
        AppDatabase.getAuthInstance()!!.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) MainActivity.launchMainActivityAsFreshStart(activity!!)
                    else {
                        Log.e(TAG, "signInWithCredential:failure + ", it.exception)
                        enableButtons()
                    }
                }
    }

    private fun sendUserPasswordResetEmail(userEmail: String) {
        AppDatabase.getAuthInstance()!!.sendPasswordResetEmail(userEmail).addOnCompleteListener {
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

    private fun disableButtons() {
        btnLogin.isEnabled = false
        btnLogin.background.setColorFilter(ContextCompat.getColor(activity!!, R.color.colorIntroUnselectedIndicator), PorterDuff.Mode.MULTIPLY)
        btnGoogleLogin.isEnabled = false
        btnGoogleLogin.background.setColorFilter(ContextCompat.getColor(activity!!, R.color.colorIntroUnselectedIndicator), PorterDuff.Mode.MULTIPLY)
    }

    private fun enableButtons() {
        btnLogin.isEnabled = true
        btnLogin.background.clearColorFilter()
        btnGoogleLogin.isEnabled = true
        btnGoogleLogin.background.clearColorFilter()
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)!!
            val credential = GoogleService.getAuthCredentialFromAccount(account)

            AppDatabase.getAuthInstance()!!.fetchSignInMethodsForEmail(account.email!!)
                    .addOnCompleteListener { task ->
                        //TODO: Catch network exception which is thrown when there is no internet
                        val isNewUser = task.result!!.signInMethods!!.isEmpty()

                        if (isNewUser) {
                            RegistrationFormFragment.launchRegistrationFormWithGoogleAccount(activity!!, credential, account)
                        } else {
                            signUserIntoFireAuthWithCredential(credential)
                        }
                    }
        } catch (e: ApiException) {
            Log.e(TAG, "Google sign in failed")
            Log.w(TAG, "signInResult: failed code =" + e.statusCode)
            enableButtons()
        }
    }

    companion object {
        private const val RC_GOOGLE_SIGN_IN = 444
        private val TAG = LoginFragment::class.simpleName

        fun launchLogin(activity: FragmentActivity) {
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.authenticationFrame, LoginFragment())
            transaction.commit()
        }
    }
}
