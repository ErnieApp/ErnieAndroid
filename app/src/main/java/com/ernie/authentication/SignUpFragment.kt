package com.ernie.authentication

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupBtnSignUpListener()
        setupBtnGoogleSignUpListener()
        setupExistingUserListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            handleGoogleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(data))
        }
    }

    private fun setupBtnSignUpListener() {
        btnSignUp.setOnClickListener {
            disableButtons()
            val userName = fieldName.text.toString()
            val userEmail = fieldEmail.text.toString()
            val userPassword = fieldPassword.text.toString()

            if (!isNameBlank() && isValidEmail(userEmail) && doEmailFieldsMatch() && isStrongPassword(userPassword) && doPasswordFieldsMatch()) {
                AppDatabase.getAuthInstance()!!.fetchSignInMethodsForEmail(userEmail)
                        .addOnCompleteListener { task ->
                            //TODO: Catch network exception which is thrown when there is no internet
                            val isNewUser = task.result!!.signInMethods!!.isEmpty()

                            if (isNewUser) {
                                RegistrationFormFragment.launchRegistrationFormWithNameAndEmailAndPassword(activity!!, userName, userEmail, userPassword)
                            } else {
                                fieldEmail.error = "Account already exists"
                                enableButtons()
                            }
                        }
            } else {
                enableButtons()
            }
        }
    }

    private fun setupBtnGoogleSignUpListener() {
        btnGoogleSignUp.setOnClickListener {
            disableButtons()
            startActivityForResult(GoogleService.getSignInIntentWithDefaultSignInOptions(activity!!.application), RC_GOOGLE_SIGN_IN)
        }
    }

    private fun setupExistingUserListener() {
        existingUserTextView.setOnClickListener {
            LoginFragment.launchLogin(activity!!)
        }
    }

    private fun isNameBlank(): Boolean {
        return if (fieldName.text.isBlank()) {
            fieldName.error = "Enter your name"
            true
        } else {
            fieldName.error = null
            false
        }
    }

    private fun isValidEmail(userEmail: CharSequence): Boolean {
        return if (userEmail.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            fieldEmail.error = null
            true
        } else {
            fieldEmail.error = "Invalid email"
            false
        }
    }

    private fun doEmailFieldsMatch(): Boolean {
        return if (fieldEmail.text.toString().equals(fieldConfirmEmail.text.toString())) {
            fieldConfirmEmail.error = null
            true
        } else {
            fieldConfirmEmail.error = "Email doesn't match"
            false
        }
    }

    private fun isStrongPassword(target: CharSequence): Boolean {
        val result = """^(?=.*[A-Z])(?=.*[^A-Za-z0-9])(?=.*[0-9])(?=.*[a-z]).{8,}$""".toRegex().matchEntire(target)?.groups?.get(0)?.value
        return if (result != null) {
            fieldPassword.error = null
            true
        } else {
            fieldPassword.error = "Password must include:\n" +
                    "- upper-case and lower-case letters\n" +
                    "- a number\n" +
                    "- a special character\n" +
                    "- at least 8 characters\n"
            false
        }
    }

    private fun doPasswordFieldsMatch(): Boolean {
        return if (fieldPassword.text.toString().equals(fieldConfirmPassword.text.toString())) {
            fieldConfirmPassword.error = null
            true
        } else {
            fieldConfirmPassword.error = "Password doesn't match"
            false
        }
    }

    private fun disableButtons() {
        btnSignUp.isEnabled = false
        btnSignUp.background.setColorFilter(ContextCompat.getColor(activity!!, R.color.colorIntroUnselectedIndicator), PorterDuff.Mode.MULTIPLY)
        btnGoogleSignUp.isEnabled = false
        btnGoogleSignUp.background.setColorFilter(ContextCompat.getColor(activity!!, R.color.colorIntroUnselectedIndicator), PorterDuff.Mode.MULTIPLY)
    }

    private fun enableButtons() {
        btnSignUp.isEnabled = true
        btnSignUp.background.clearColorFilter()
        btnGoogleSignUp.isEnabled = true
        btnGoogleSignUp.background.clearColorFilter()
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

    companion object {
        private const val RC_GOOGLE_SIGN_IN = 444
        private val TAG = SignUpFragment::class.simpleName

        fun launchSignUp(activity: FragmentActivity) {
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.authenticationFrame, SignUpFragment())
            transaction.commit()
        }
    }
}
