package com.ernie.authentication

import android.content.Intent
import android.os.Bundle
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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.fragment_login.fieldEmail
import kotlinx.android.synthetic.main.fragment_login.fieldPassword
import kotlinx.android.synthetic.main.fragment_sign_up.*


class SignUpFragment : Fragment() {

    val fireAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupBtnSignUpListener()
        setupBtnGoogleSignUpListener()
        setupExistingUserListener()
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return target.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun isStrongPassword(target: CharSequence): Boolean {
        val result = """^(?=.*[A-Z])(?=.*[^A-Za-z0-9])(?=.*[0-9])(?=.*[a-z]).{8,}$""".toRegex().matchEntire(target)?.groups?.get(0)?.value
        return result != null
    }

    private fun guideUserHome() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        activity!!.finish()
    }

    private fun setupExistingUserListener() {
        existingUserTextView.setOnClickListener {
            val fragmentManager: FragmentManager = activity!!.supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.authenticationFrame, LoginFragment())
            transaction.commit()
        }
    }

    private fun setupBtnSignUpListener() {
        btnSignUp.setOnClickListener {
            val userEmail = fieldEmail.text.toString()
            val userPassword = fieldPassword.text.toString()

            if (isValidEmail(userEmail) && isStrongPassword(userPassword) && doEmailFieldsMatch() && doPasswordFieldsMatch()) {
                val fragmentManager: FragmentManager = activity!!.supportFragmentManager
                val transaction = fragmentManager.beginTransaction()

                val bundle = Bundle()
                bundle.putBoolean("isGoogle", false)
                bundle.putString("userEmail", userEmail)
                bundle.putString("userPassword", userPassword)

                val registrationFormFragment = RegistrationFormFragment()
                registrationFormFragment.arguments = bundle

                transaction.replace(R.id.authenticationFrame, registrationFormFragment)
                transaction.commit()
            } else {
                if (!isValidEmail(userEmail)) {
                    fieldEmail.error = "Invalid email"
                } else if (!doEmailFieldsMatch()) {
                    fieldConfirmEmail.error = "Email doesn't match"
                }

                if (!isStrongPassword(userPassword)) {
                    fieldPassword.error = "Password must include:\n" +
                            "- upper-case and lower-case letters\n" +
                            "- a number\n" +
                            "- a special character\n" +
                            "- at least 8 characters\n"
                } else if (!doPasswordFieldsMatch()) {
                    fieldConfirmPassword.error = "Password doesn't match"
                }
            }
        }
    }

    private fun doEmailFieldsMatch(): Boolean {
        return fieldEmail.text.toString().equals(fieldConfirmEmail.text.toString())
    }

    private fun doPasswordFieldsMatch(): Boolean {
        return fieldPassword.text.toString().equals(fieldConfirmPassword.text.toString())
    }

    private fun setupBtnGoogleSignUpListener() {
        btnGoogleSignUp.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

            val signInIntent = GoogleSignIn.getClient(activity!!.application, gso).signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account!!)
        } catch (e: ApiException) {
            Log.e("JKE", "Google sign in failed")
            Log.w("JKE", "signInResult:failed code=" + e.statusCode)
        }

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("JKE", "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        fireAuth.fetchSignInMethodsForEmail(acct.email!!)
                .addOnCompleteListener { task ->
                    val isNewUser = task.result!!.signInMethods!!.isEmpty()

                    if (isNewUser) {
                        Log.e("TAG", "Is New User!")

                        //TODO: Collect more data before creating their account

                        val fragmentManager: FragmentManager = activity!!.supportFragmentManager
                        val transaction = fragmentManager.beginTransaction()

                        val bundle = Bundle()
                        bundle.putBoolean("isGoogle", true)
                        bundle.putParcelable("credential", credential)

                        val registrationFormFragment = RegistrationFormFragment()
                        registrationFormFragment.arguments = bundle

                        transaction.replace(R.id.authenticationFrame, registrationFormFragment)
                        transaction.commit()

                    } else {
                        Toast.makeText(activity, "Already have an account, logging in...", Toast.LENGTH_LONG)
                        fireAuth.signInWithCredential(credential)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("JKE", "signInWithCredential:success")
                                        guideUserHome()
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("JKE", "signInWithCredential:failure", task.exception)
                                        Toast.makeText(activity, "FAILED", Toast.LENGTH_LONG)
                                    }
                                }

                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    companion object {
        private const val RC_GOOGLE_SIGN_IN = 444
    }
}
