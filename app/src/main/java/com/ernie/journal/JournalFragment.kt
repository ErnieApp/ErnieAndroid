package com.ernie.journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ernie.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_journal.*

class JournalFragment : Fragment() {

    private lateinit var journalListAddEntryFragment: JournalListAddEntryFragment
    private lateinit var journalListFragment: JournalListFragment
    private lateinit var journalListExpandedEntryFragment: JournalListExpandedEntryFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        journalListAddEntryFragment = JournalListAddEntryFragment()
        journalListFragment = JournalListFragment()
        journalListExpandedEntryFragment = JournalListExpandedEntryFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_journal, container, false)
        setupFloatingActionButtonListener(view.findViewById(R.id.floatingActionButton))
        showEntryList()
        return view
    }

    private fun setupFloatingActionButtonListener(button: FloatingActionButton) {
        button.setOnClickListener {
            if (journalListAddEntryFragment.isVisible) {
                showEntryList()
            } else {
                showEntryAddForm()
            }

            if (floatingActionButton.rotation != 45f) {
                floatingActionButton.rotation += 45
            } else {
                floatingActionButton.rotation -= floatingActionButton.rotation
            }
        }
    }

    private fun showEntryList() {
        replaceCurrentFragment(R.id.journalFrameContainer, journalListFragment)
    }

    private fun showEntryAddForm() {
        replaceCurrentFragment(R.id.journalFrameContainer, journalListAddEntryFragment)
    }

    private fun showExpandedEntry() {
        replaceCurrentFragment(R.id.journalFrameContainer, journalListExpandedEntryFragment)
    }

    private fun replaceCurrentFragment(@IdRes containerViewId: Int, fragment: Fragment) {
        val fragmentManager: FragmentManager = activity!!.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(containerViewId, fragment)
        transaction.commit()
    }

    companion object {
        private const val TAG = "JournalFragment"
    }
}
