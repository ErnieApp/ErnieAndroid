package com.ernie.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ernie.R

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [introSlide1.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [introSlide1.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class introSlide1Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intro_slide1, container, false)
    }

    companion object {

        fun newInstance(): introSlide1Fragment {
            return introSlide1Fragment()
        }
    }
}
