package com.ernie

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import com.ernie.intro.introSlide1Fragment
import com.ernie.intro.introSlide2Fragment
import com.ernie.intro.introSlide3Fragment
import com.ernie.intro.introSlide4Fragment
import com.github.paolorotolo.appintro.AppIntro
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppIntro() {

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

        }

        log_in.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_intro
    }
}
