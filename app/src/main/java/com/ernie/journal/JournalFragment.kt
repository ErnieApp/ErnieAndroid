package com.ernie.journal

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ernie.R

class JournalFragment : Fragment() {

    companion object {

        fun newInstance(): JournalFragment {
            return JournalFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_journal, container, false)
        val fragment = fragmentManager!!.findFragmentById(R.id.fragmentContainer)
        if (fragment == null) {
            fragmentManager!!.beginTransaction()
                    .add(R.id.fragmentContainer, JournalListFragment.newInstance(), "list")
                    .commit()
        }
        return view
    }
}