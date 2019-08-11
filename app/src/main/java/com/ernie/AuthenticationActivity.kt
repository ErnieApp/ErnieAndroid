package com.ernie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.ernie.authentication.LoginFragment
import com.ernie.authentication.SignUpFragment


class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        val bundle = intent.extras
        val shouldLaunchLogin = bundle.getBoolean("shouldLaunchLogin")

        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        if (shouldLaunchLogin) {
            transaction.add(R.id.authenticationFrame, LoginFragment(), "LOGIN_FRAGMENT")
        } else {
            transaction.add(R.id.authenticationFrame, SignUpFragment(), "SIGNUP_FRAGMENT")
        }
        transaction.commit()
    }

}
