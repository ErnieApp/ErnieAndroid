package com.ernie.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ernie.R

class introSlide3Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intro_slide3, container, false)
    }

    companion object {

        fun newInstance(): introSlide3Fragment {
            return introSlide3Fragment()
        }
    }
}
