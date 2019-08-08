package com.ernie.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ernie.R

class introSlide4Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro_slide4, container, false)
    }

    companion object {

        fun newInstance(): introSlide4Fragment {
            return introSlide4Fragment()
        }
    }
}
