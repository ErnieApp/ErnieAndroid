package com.ernie

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.ernie.home.HomeFragment
import com.ernie.journal.JournalFragment
import com.ernie.profile.ProfileFragment
import com.github.paolorotolo.appintro.AppIntro
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fireAuth = FirebaseAuth.getInstance()

        if (fireAuth.currentUser == null) {
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            finish()
        }

        addSlide(homeFragment)
        addSlide(journalFragment)
        addSlide(profileFragment)

        showPagerIndicator(false)
        showSkipButton(false)

        home.setOnClickListener { pager.setCurrentItem(0, true) }
        journal.setOnClickListener { pager.setCurrentItem(1, true) }
        profile.setOnClickListener { pager.setCurrentItem(2, true) }

        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (slides[position] == homeFragment) {
                    clearSelectedButton()
                    home.setImageResource(R.drawable.ic_home_blue_24dp)
                } else if (slides[position] == journalFragment) {
                    clearSelectedButton()
                    journal.setImageResource(R.drawable.ic_dashboard_blue_24dp)
                } else if (slides[position] == profileFragment) {
                    clearSelectedButton()
                    profile.setImageResource(R.drawable.ic_notifications_blue_24dp)
                }
            }
        })
    }

    private fun clearSelectedButton() {
        home.setImageResource(R.drawable.ic_home_black_24dp)
        journal.setImageResource(R.drawable.ic_dashboard_black_24dp)
        profile.setImageResource(R.drawable.ic_notifications_black_24dp)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onPause() {
        super.onPause()
        val fireAuth = FirebaseAuth.getInstance()
        if (fireAuth.currentUser != null) {
            fireAuth.currentUser!!.reload().addOnFailureListener {
                fireAuth.signOut()
                finish()
            }
        }
    }

    override fun onBackPressed() {
        if (slides[pager.currentItem] == journalFragment && (journalFragment.isAddEntryFormVisible() || journalFragment.isExpandedEntryVisible())) {
            journalFragment.clickFloatingActionButton()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private val homeFragment = HomeFragment()
        private val journalFragment = JournalFragment()
        private val profileFragment = ProfileFragment()

        fun launchMainActivityAsFreshStart(activity: Activity) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            activity.finish()
        }
    }
}