package com.ernie.journal

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ernie.R
import kotlinx.android.synthetic.main.fragment_journal.*


private const val TAG = "JournalFragment"

class JournalFragment : Fragment() {

    private lateinit var journalListAddEntryFragment: JournalListAddEntryFragment
    private lateinit var journalListFragment: JournalListFragment
    private lateinit var journalListExpandedEntryFragment: JournalListExpandedEntryFragment

    companion object {

        fun newInstance(): JournalFragment {
            return JournalFragment()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            journalListFragment = JournalListFragment.newInstance()
            journalListAddEntryFragment = JournalListAddEntryFragment.newInstance()
            journalListExpandedEntryFragment = JournalListExpandedEntryFragment.newInstance()
            journalListFragment.setJournalFragment(this)
        } else {
            journalListFragment = savedInstanceState.get("entryList") as JournalListFragment
            journalListAddEntryFragment = savedInstanceState.get("entryAddForm") as JournalListAddEntryFragment
            journalListExpandedEntryFragment = savedInstanceState.get("entryExpanded") as JournalListExpandedEntryFragment
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("entryList", journalListFragment)
        outState.putSerializable("entryAddForm", journalListAddEntryFragment)
        outState.putSerializable("entryExpanded", journalListExpandedEntryFragment)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_journal, container, false)


        displayFragmentA()

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        floatingActionButton.setOnClickListener { view ->
            displayFragmentB()
        }
        //Begin Transcation

    }


    protected fun displayFragmentA() {
        val ft = childFragmentManager.beginTransaction()

        if (journalListFragment.isAdded) {

            ft.show(journalListFragment)
        } else {
            ft.add(R.id.fragmentContainer, journalListFragment, "journalListFragment")
        }
        // Hide fragment
        if (journalListAddEntryFragment.isAdded) {
            ft.hide(journalListAddEntryFragment)
        }
        if (journalListExpandedEntryFragment.isAdded) {
            ft.hide(journalListExpandedEntryFragment)
        }

        // Commit changes
        ft.commit()
    }


    protected fun displayFragmentB() {
        val ft = childFragmentManager.beginTransaction()

        if (journalListAddEntryFragment.isAdded) {

            ft.show(journalListAddEntryFragment)
        } else {

            ft.add(R.id.fragmentContainer, journalListAddEntryFragment, "journalListFragment")

        }
        // Hide fragment
        if (journalListFragment.isAdded) {
            ft.hide(journalListFragment)
        }
        if (journalListExpandedEntryFragment.isAdded) {
            ft.hide(journalListExpandedEntryFragment)
        }

        // Commit changes
        ft.commit()
    }

    fun displayFragmentC() {
        val ft = childFragmentManager.beginTransaction()

        if (journalListExpandedEntryFragment.isAdded) {

            ft.show(journalListExpandedEntryFragment)
        } else {

            ft.add(R.id.fragmentContainer, journalListExpandedEntryFragment, "journalListFragment")

        }
        // Hide fragment
        if (journalListFragment.isAdded) {
            ft.hide(journalListFragment)
        }
        if (journalListAddEntryFragment.isAdded) {
            ft.hide(journalListAddEntryFragment)
        }

        // Commit changes
        ft.commit()
    }

}


interface OnFragmentInteractionListener {
    fun onFragmentInteraction(uri: Uri)
}

