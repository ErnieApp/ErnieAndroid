package com.ernie

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.ernie.intro.IntroSlide1Fragment
import com.ernie.intro.IntroSlide2Fragment
import com.ernie.intro.IntroSlide3Fragment
import com.ernie.intro.IntroSlide4Fragment
import com.github.paolorotolo.appintro.AppIntro
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(IntroSlide1Fragment())
        addSlide(IntroSlide2Fragment())
        addSlide(IntroSlide3Fragment())
        addSlide(IntroSlide4Fragment())

        setIndicatorColor(R.color.colorIntroSelectedIndicator, R.color.colorIntroUnselectedIndicator)

        val previousButton = findViewById<ImageButton>(com.github.paolorotolo.appintro.R.id.back)
        previousButton.setColorFilter(R.color.colorIntroText)
        setNextArrowColor(ContextCompat.getColor(this, R.color.colorIntroText))
        // The following works as an alternate but raises a false-error
        // setNextArrowColor(R.color.colorIntroText)

        showSkipButton(false)
        wizardMode = true
        backButtonVisibilityWithDone = true

        get_started.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("shouldLaunchLogin", false)
            startAuthenticationActivityWithSuppliedBundle(bundle)
        }

        log_in.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("shouldLaunchLogin", true)
            startAuthenticationActivityWithSuppliedBundle(bundle)
        }
    }

    private fun startAuthenticationActivityWithSuppliedBundle(bundle: Bundle) {
        val intent = Intent(this, AuthenticationActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_intro
    }
}
