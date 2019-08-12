package com.ernie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.ernie.home.HomeFragment
import com.ernie.journal.JournalFragment
import com.ernie.profile.ProfileFragment
import com.github.paolorotolo.appintro.AppIntro
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"

class MainActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fireAuth = FirebaseAuth.getInstance()


        if (fireAuth.currentUser == null) {
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            finish()
        }

        addSlide(HomeFragment())
        addSlide(JournalFragment())
        addSlide(ProfileFragment())

        showPagerIndicator(false)
        showSkipButton(false)

        home.setOnClickListener {
            
        }

        journal.setOnClickListener {

        }

        profile.setOnClickListener {

        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onPause() {
        super.onPause()
        val fireAuth = FirebaseAuth.getInstance()
        if (fireAuth.currentUser != null) {
            Log.d("MERT", "reloading ...")
            fireAuth.currentUser!!.reload().addOnFailureListener {
                fireAuth.signOut()
                finish()
            }
        }
    }

}