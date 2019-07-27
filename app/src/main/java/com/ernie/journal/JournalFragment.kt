package com.ernie.journal


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ernie.R

private const val TAG = "Journal Class"


class JournalFragment : Fragment() {
    private lateinit var viewPager: ViewPager
    private lateinit var tabs: TabLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreate: starts")
        val view: View = inflater.inflate(R.layout.fragment_journal, container, false)

        addFragment()
        Log.d(TAG, "onCreate: ends")
        return view
    }

    private fun addFragment() {

        val newFragment = JournalListFragmentViewModel.newInstance("Hello", "World")
        fragmentManager!!.beginTransaction()
                .replace(R.id.fragmentContainer, newFragment)
                .commit()


    }

} // Required empty public  constructor