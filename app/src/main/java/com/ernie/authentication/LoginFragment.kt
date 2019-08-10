package com.ernie.authentication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {

    val fireAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupBtnLoginListener()
        setupBtnGoogleLoginListener()
        setupForgotPasswordListener()
        setupNewUserListener()
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun guideUserHome() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        activity!!.finish()
    }

    private fun setupNewUserListener() {
        newUserTextView.setOnClickListener {
            val fragmentManager: FragmentManager = activity!!.supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.authenticationFrame, SignUpFragment())
            transaction.commit()
        }
    }

    private fun setupForgotPasswordListener() {
        forgotPasswordTextView.setOnClickListener {
            val userEmail = fieldEmail.text.toString()
            if (isValidEmail(userEmail)) {
                fireAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(activity, "Email sent", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                fieldPassword.error = null
                if (userEmail.isBlank()) {
                    fieldEmail.error = "Enter your email"
                } else {
                    fieldEmail.error = "Invalid email"
                }
            }
        }
    }

    private fun setupBtnLoginListener() {
        btnLogin.setOnClickListener {
            val userEmail = fieldEmail.text.toString()
            val userPassword = fieldPassword.text.toString()

            if (isValidEmail(userEmail) && userPassword.isNotBlank()) {
                fireAuth.signInWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener {
                            // Check if user has logged in successfully
                            if (fireAuth.currentUser != null) {
                                guideUserHome()
                            } else {
                                fieldPassword.error = "Incorrect password"
                            }
                        }
            } else {
                if (userEmail.isBlank()) {
                    fieldEmail.error = "Enter your email"
                } else {
                    fieldEmail.error = "Invalid email"
                }

                if (userPassword.isBlank()) {
                    fieldPassword.error = "Enter your password"
                }
            }
        }
    }

    private fun setupBtnGoogleLoginListener() {
        btnGoogleLogin.setOnClickListener {
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

                    } else {
                        Log.e("TAG", "Is Old User!")

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
